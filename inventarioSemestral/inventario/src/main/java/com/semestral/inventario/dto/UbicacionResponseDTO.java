package com.semestral.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UbicacionResponseDTO {

    private Long idPasEst; 
    private String nombrePasillo;
    private String nombreEstante;
}
