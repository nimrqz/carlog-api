package com.carlog.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleExpenseSummaryDTO {

    private Long vehicleId;
    private String vehiclePlate;
    private String vehicleModel;
    private Integer currentMileage;
    private Integer nextOilChangeMileage;
    private BigDecimal totalSpent;
    private Long totalMaintenances;
}
