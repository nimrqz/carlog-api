package com.carlog.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {

    private Long id;

    @NotBlank(message = "A descricao e obrigatoria")
    @Size(min = 3, max = 255, message = "Descricao deve ter entre 3 e 255 caracteres")
    private String description;

    @NotNull(message = "A data e obrigatoria")
    private LocalDate date;

    @NotNull(message = "O valor e obrigatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor nao pode ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Valor invalido")
    private BigDecimal value;

    @NotNull(message = "A quilometragem no momento e obrigatoria")
    @Min(value = 0, message = "Quilometragem nao pode ser negativa")
    private Integer mileageAtTime;

    @NotNull(message = "O ID do veiculo e obrigatorio")
    private Long vehicleId;
}
