package com.semestral.gestion_direccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionResponseDTO {
    private Long idDireccion;
    private String calle;
    private String numero;
    private String comuna;
    private String region;
}
