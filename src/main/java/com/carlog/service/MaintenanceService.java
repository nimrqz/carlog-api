package com.carlog.service;

import com.carlog.dto.MaintenanceDTO;
import com.carlog.dto.MaintenanceResponseDTO;
import com.carlog.dto.VehicleExpenseSummaryDTO;
import com.carlog.exception.BusinessException;
import com.carlog.exception.ResourceNotFoundException;
import com.carlog.model.Maintenance;
import com.carlog.model.Vehicle;
import com.carlog.repository.MaintenanceRepository;
import com.carlog.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private static final int OIL_CHANGE_INTERVAL_KM = 10000;

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public MaintenanceResponseDTO registerMaintenance(MaintenanceDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado com ID: " + dto.getVehicleId()));

        // Regra de negocio: nao permitir quilometragem menor que a atual do carro
        if (dto.getMileageAtTime() < vehicle.getMileage()) {
            throw new BusinessException(
                    "A quilometragem da manutencao (" + dto.getMileageAtTime() +
                    ") nao pode ser menor que a quilometragem atual do veiculo (" + vehicle.getMileage() + ")"
            );
        }

        // Regra de negocio: atualizar km do carro automaticamente
        if (dto.getMileageAtTime() > vehicle.getMileage()) {
            vehicle.setMileage(dto.getMileageAtTime());
            vehicleRepository.save(vehicle);
        }

        // Calculo automatico da proxima troca de oleo
        Integer nextOilChange = null;
        String descLower = dto.getDescription().toLowerCase();
        if (descLower.contains("oleo") || descLower.contains("oleo") || descLower.contains("troca de oleo")) {
            nextOilChange = dto.getMileageAtTime() + OIL_CHANGE_INTERVAL_KM;
        }

        Maintenance maintenance = Maintenance.builder()
                .description(dto.getDescription().trim())
                .date(dto.getDate())
                .value(dto.getValue())
                .mileageAtTime(dto.getMileageAtTime())
                .nextOilChangeMileage(nextOilChange)
                .vehicle(vehicle)
                .build();

        Maintenance saved = maintenanceRepository.save(maintenance);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaintenanceResponseDTO findById(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutencao nao encontrada com ID: " + id));
        return toResponseDTO(maintenance);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> findByVehicleId(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Veiculo nao encontrado com ID: " + vehicleId);
        }
        return maintenanceRepository.findByVehicleId(vehicleId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaintenanceResponseDTO update(Long id, MaintenanceDTO dto) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutencao nao encontrada com ID: " + id));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado com ID: " + dto.getVehicleId()));

        // Se a quilometragem do momento mudou, validar e atualizar veiculo
        if (!maintenance.getMileageAtTime().equals(dto.getMileageAtTime())) {
            if (dto.getMileageAtTime() < vehicle.getMileage() && dto.getMileageAtTime() < maintenance.getMileageAtTime()) {
                throw new BusinessException(
                        "A nova quilometragem da manutencao nao pode ser menor que a atual do veiculo"
                );
            }
            if (dto.getMileageAtTime() > vehicle.getMileage()) {
                vehicle.setMileage(dto.getMileageAtTime());
                vehicleRepository.save(vehicle);
            }
        }

        Integer nextOilChange = maintenance.getNextOilChangeMileage();
        String descLower = dto.getDescription().toLowerCase();
        if (descLower.contains("oleo") || descLower.contains("oleo") || descLower.contains("troca de oleo")) {
            nextOilChange = dto.getMileageAtTime() + OIL_CHANGE_INTERVAL_KM;
        } else {
            nextOilChange = null;
        }

        maintenance.setDescription(dto.getDescription().trim());
        maintenance.setDate(dto.getDate());
        maintenance.setValue(dto.getValue());
        maintenance.setMileageAtTime(dto.getMileageAtTime());
        maintenance.setNextOilChangeMileage(nextOilChange);
        maintenance.setVehicle(vehicle);

        Maintenance updated = maintenanceRepository.save(maintenance);
        return toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manutencao nao encontrada com ID: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public VehicleExpenseSummaryDTO getExpenseSummary(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado com ID: " + vehicleId));

        BigDecimal totalSpent = maintenanceRepository.sumValuesByVehicleId(vehicleId);
        Long totalMaintenances = (long) maintenanceRepository.findByVehicleId(vehicleId).size();

        // Encontra a proxima troca de oleo (maior valor)
        Integer nextOilChange = maintenanceRepository.findByVehicleId(vehicleId).stream()
                .filter(m -> m.getNextOilChangeMileage() != null)
                .map(Maintenance::getNextOilChangeMileage)
                .max(Integer::compare)
                .orElse(null);

        return VehicleExpenseSummaryDTO.builder()
                .vehicleId(vehicle.getId())
                .vehiclePlate(vehicle.getPlate())
                .vehicleModel(vehicle.getModel())
                .currentMileage(vehicle.getMileage())
                .nextOilChangeMileage(nextOilChange)
                .totalSpent(totalSpent)
                .totalMaintenances(totalMaintenances)
                .build();
    }

    private MaintenanceResponseDTO toResponseDTO(Maintenance maintenance) {
        return MaintenanceResponseDTO.builder()
                .id(maintenance.getId())
                .description(maintenance.getDescription())
                .date(maintenance.getDate())
                .value(maintenance.getValue())
                .mileageAtTime(maintenance.getMileageAtTime())
                .nextOilChangeMileage(maintenance.getNextOilChangeMileage())
                .vehicleId(maintenance.getVehicle().getId())
                .vehiclePlate(maintenance.getVehicle().getPlate())
                .build();
    }
}
