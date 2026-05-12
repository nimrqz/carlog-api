package com.carlog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    @NotBlank(message = "A placa e obrigatoria")
    private String plate;

    @Column(nullable = false)
    @NotBlank(message = "O modelo e obrigatorio")
    private String model;

    @Column(nullable = false)
    @NotBlank(message = "A marca e obrigatoria")
    private String brand;

    @Column(nullable = false, name = "\"year\"")
    @Min(value = 1900, message = "Ano deve ser no minimo 1900")
    @Max(value = 2100, message = "Ano deve ser no maximo 2100")
    private Integer year;

    @Column(nullable = false)
    @Min(value = 0, message = "Quilometragem nao pode ser negativa")
    private Integer mileage;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Maintenance> maintenances = new ArrayList<>();
}
