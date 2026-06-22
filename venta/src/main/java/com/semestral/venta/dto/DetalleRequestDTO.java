package com.semestral.venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleRequestDTO {
    
    @Schema(
        description = "Cantidad del producto en el detalle de venta",
        example = "2"
    )
    @NotNull(message = "La cantidad no puede estar vacia")
    private Integer cantidad;
    
    @Schema(
        description = "ID de la venta",
        example = "1"
    )
    @NotNull(message = "La ventaId es obligatoria")
    private Long ventaId;
    

    @Schema(
        description = "ID del producto en el detalle de venta",
        example = "1"
    )
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

}
