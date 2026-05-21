package com.semestral.inventario.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasilloResponseDTO {


    private Long idPasillo;

    private String nombrePasillo;
}
