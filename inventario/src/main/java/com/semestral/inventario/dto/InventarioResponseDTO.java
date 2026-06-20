package com.semestral.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioResponseDTO {

    private Long idPps;            
    private Long idProd;
    private String skuProducto;
    private String nombreProducto;
    private Double precioProducto;
    private String nombrePasillo;   
    private String nombreEstante;   
    private Integer stock;        
}
