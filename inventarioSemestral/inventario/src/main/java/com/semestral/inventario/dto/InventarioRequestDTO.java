package com.semestral.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioRequestDTO {

    private Long idProd;   
    private Long idPasillo;    
    private Long idEstante;    
    private Integer cantidad;  

}
