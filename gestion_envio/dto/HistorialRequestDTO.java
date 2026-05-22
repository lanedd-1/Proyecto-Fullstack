package com.joyeria.gestion_envio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialRequestDTO {
    private Long envioId;
    private String estado;
    private Long usuarioId; 
}
