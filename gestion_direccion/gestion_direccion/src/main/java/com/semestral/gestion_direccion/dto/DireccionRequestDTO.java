package com.semestral.gestion_direccion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionRequestDTO {
    @NotBlank
    private String calle;
    @NotBlank
    private String numero;
    @NotNull
    @Positive
    private Long idComuna;
}
