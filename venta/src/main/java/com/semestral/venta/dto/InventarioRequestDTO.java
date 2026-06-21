package com.semestral.venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioRequestDTO {

    @Schema(description = "ID del producto", example = "1")
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long idProd;

    @Schema(description = "ID del pasillo", example = "1")
    private Long idPasillo;

    @Schema(description = "ID del estante", example = "1")
    private Long idEstante;

    @Schema(description = "Cantidad a descontar o agregar", example = "10")
    @NotNull(message = "La cantidad no puede ser nula")
    private Integer cantidad;
}
