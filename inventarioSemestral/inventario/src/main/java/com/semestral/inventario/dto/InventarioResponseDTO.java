package com.semestral.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioResponseDTO {

    private Long idPps;             // id unico
    private Long idProducto;
    private String nombrePasillo;   // en que pasillo esta
    private String nombreEstante;   // en que estante esta
    private Integer stock;          // la cantidad disponible en este lugar específico
}
