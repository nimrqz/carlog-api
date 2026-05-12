package com.carlog.controller;

import com.carlog.dto.MaintenanceDTO;
import com.carlog.dto.MaintenanceResponseDTO;
import com.carlog.dto.VehicleExpenseSummaryDTO;
import com.carlog.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<MaintenanceResponseDTO> create(@Valid @RequestBody MaintenanceDTO dto) {
        MaintenanceResponseDTO created = maintenanceService.registerMaintenance(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceResponseDTO>> findAll() {
        return ResponseEntity.ok(maintenanceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.findById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<MaintenanceResponseDTO>> findByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.findByVehicleId(vehicleId));
    }

    @GetMapping("/vehicle/{vehicleId}/expenses")
    public ResponseEntity<VehicleExpenseSummaryDTO> getExpenseSummary(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.getExpenseSummary(vehicleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MaintenanceDTO dto) {
        return ResponseEntity.ok(maintenanceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
