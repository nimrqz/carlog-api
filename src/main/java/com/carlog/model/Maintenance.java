package com.carlog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "A descricao e obrigatoria")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "A data e obrigatoria")
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2, name = "\"value\"")
    @NotNull(message = "O valor e obrigatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor nao pode ser negativo")
    private BigDecimal value;

    @Column(nullable = false, name = "mileage_at_time")
    @NotNull(message = "A quilometragem no momento e obrigatoria")
    @Min(value = 0, message = "Quilometragem nao pode ser negativa")
    private Integer mileageAtTime;

    @Column(name = "next_oil_change_mileage")
    private Integer nextOilChangeMileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "O veiculo e obrigatorio")
    private Vehicle vehicle;
}
