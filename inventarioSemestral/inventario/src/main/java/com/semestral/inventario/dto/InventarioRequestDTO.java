package com.semestral.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioRequestDTO {

    private Long idProducto;   
    private Long idPasillo;    // A que pasillo va
    private Long idEstante;    // A que estante va
    private Integer cantidad;  // Cuantas unidades se van a sumar o restar

}
