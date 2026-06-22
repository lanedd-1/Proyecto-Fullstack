package com.semestral.inventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    
    @Schema(
        description = "ID del producto",
        example = "1"
    )
    @JsonProperty("idProd")
    private Long idProd;
    
    @Schema(
        description = "SKU del producto",
        example = "PROD-001"
    )
    @JsonProperty("sku")
    private String sku;
    
    @Schema(
        description = "Nombre del producto",
        example = "Producto A"
    )
    @JsonProperty("nombreProd")
    private String nombreProd;
    
    @Schema(
        description = "Precio unitario del producto",
        example = "10000.00"
    )
    @JsonProperty("precioUnitario")
    private Double precioUnitario;
}
