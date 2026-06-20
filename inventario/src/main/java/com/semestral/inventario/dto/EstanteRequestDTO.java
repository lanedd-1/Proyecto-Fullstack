package com.semestral.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstanteRequestDTO {

    @Schema(
        description = "Nombre del estante",
        example = "Estante A"
    )
    @NotBlank(message = "El nombre del estante no puede ser nulo")
    private String nombreEstante;
}
