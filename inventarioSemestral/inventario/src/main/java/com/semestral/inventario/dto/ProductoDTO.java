package com.semestral.inventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    
    @JsonProperty("idProd")
    private Long idProd;
    
    @JsonProperty("sku")
    private String sku;
    
    @JsonProperty("nombreProd")
    private String nombreProd;
    
    @JsonProperty("precioUnitario")
    private Double precioUnitario;
}
