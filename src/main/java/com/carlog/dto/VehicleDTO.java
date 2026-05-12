package com.carlog.dto;

import com.carlog.validation.ValidPlate;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {

    private Long id;

    @NotBlank(message = "A placa e obrigatoria")
    @ValidPlate(message = "Placa invalida. Use o formato AAA-0000 ou AAA0A00")
    private String plate;

    @NotBlank(message = "O modelo e obrigatorio")
    @Size(min = 2, max = 100, message = "Modelo deve ter entre 2 e 100 caracteres")
    private String model;

    @NotBlank(message = "A marca e obrigatoria")
    @Size(min = 2, max = 100, message = "Marca deve ter entre 2 e 100 caracteres")
    private String brand;

    @NotNull(message = "O ano e obrigatorio")
    @Min(value = 1900, message = "Ano deve ser no minimo 1900")
    @Max(value = 2100, message = "Ano deve ser no maximo 2100")
    private Integer year;

    @NotNull(message = "A quilometragem e obrigatoria")
    @Min(value = 0, message = "Quilometragem nao pode ser negativa")
    private Integer mileage;
}
