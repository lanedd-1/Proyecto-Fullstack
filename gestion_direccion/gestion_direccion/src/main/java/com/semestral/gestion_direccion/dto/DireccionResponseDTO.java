package com.semestral.gestion_direccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionResponseDTO {
    private Long id_direccion;
    private String calle;
    private String numero;
    private Long id_comuna;
    private Long id_region;
}
