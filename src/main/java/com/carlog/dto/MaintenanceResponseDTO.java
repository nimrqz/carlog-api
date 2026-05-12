package com.carlog.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponseDTO {

    private Long id;
    private String description;
    private LocalDate date;
    private BigDecimal value;
    private Integer mileageAtTime;
    private Integer nextOilChangeMileage;
    private Long vehicleId;
    private String vehiclePlate;
}
