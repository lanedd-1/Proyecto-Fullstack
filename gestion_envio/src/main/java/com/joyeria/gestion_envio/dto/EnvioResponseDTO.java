package com.joyeria.gestion_envio.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvioResponseDTO {
    private Long idEnvio;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaRecepcion;
    private Long idVenta;
    private Long idDireccion;
    private String estado;
}