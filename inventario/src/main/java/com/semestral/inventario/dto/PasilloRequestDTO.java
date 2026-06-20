package com.semestral.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasilloRequestDTO {

    @Schema(
        description = "Nombre del pasillo",
        example = "Pasillo A"
    )
    @NotBlank(message = "El nombre del pasillo no puede ser nulo")
    private String nombrePasillo;
}
