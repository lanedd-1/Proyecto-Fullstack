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
    @NotBlank(message = "La calle no puede estar vacia")
    private String calle;
    @NotBlank(message = "El numero no puede estar vacio")
    private String numero;
    @NotNull(message = "El id no puede ser nula")
    @Positive(message = "el id debe ser mayor a 0")
    private Long idComuna;
}
