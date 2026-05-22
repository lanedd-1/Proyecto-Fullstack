package com.joyeria.gestion_envio.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvioRequestDTO {
    
    private LocalDateTime fechaEnvio;
    
    // La fecha de recepción puede venir nula al crear el envío, 
    // se actualiza cuando el paquete llega.
    private LocalDateTime fechaRecepcion; 

    @NotNull(message = "El ID de la venta es obligatorio")
    private Long idVenta;

    @NotNull(message = "El ID de la dirección es obligatorio")
    private Long idDireccion;

    private String estado;
}
