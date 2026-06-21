package com.semestral.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DescontarProductoRequestDTO {

    @Schema(description = "ID del producto", example = "1")
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long idProd;

    @Schema(description = "Cantidad a descontar", example = "1")
    @NotNull(message = "La cantidad no puede ser nula")
    private Integer cantidad;
}
