package com.carlog.service;

import com.carlog.dto.VehicleDTO;
import com.carlog.exception.BusinessException;
import com.carlog.exception.ResourceNotFoundException;
import com.carlog.model.Vehicle;
import com.carlog.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public VehicleDTO create(VehicleDTO dto) {
        String normalizedPlate = dto.getPlate().trim().toUpperCase().replace("-", "");

        if (vehicleRepository.existsByPlate(normalizedPlate)) {
            throw new BusinessException("Ja existe um veiculo cadastrado com esta placa: " + dto.getPlate());
        }

        Vehicle vehicle = Vehicle.builder()
                .plate(normalizedPlate)
                .model(dto.getModel().trim())
                .brand(dto.getBrand().trim())
                .year(dto.getYear())
                .mileage(dto.getMileage())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleDTO findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado com ID: " + id));
        return toDTO(vehicle);
    }

    @Transactional
    public VehicleDTO update(Long id, VehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado com ID: " + id));

        String normalizedPlate = dto.getPlate().trim().toUpperCase().replace("-", "");

        if (!vehicle.getPlate().equalsIgnoreCase(normalizedPlate) && vehicleRepository.existsByPlate(normalizedPlate)) {
            throw new BusinessException("Ja existe outro veiculo cadastrado com esta placa: " + dto.getPlate());
        }

        vehicle.setPlate(normalizedPlate);
        vehicle.setModel(dto.getModel().trim());
        vehicle.setBrand(dto.getBrand().trim());
        vehicle.setYear(dto.getYear());
        vehicle.setMileage(dto.getMileage());

        Vehicle updated = vehicleRepository.save(vehicle);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Veiculo nao encontrado com ID: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    private VehicleDTO toDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate())
                .model(vehicle.getModel())
                .brand(vehicle.getBrand())
                .year(vehicle.getYear())
                .mileage(vehicle.getMileage())
                .build();
    }
}
