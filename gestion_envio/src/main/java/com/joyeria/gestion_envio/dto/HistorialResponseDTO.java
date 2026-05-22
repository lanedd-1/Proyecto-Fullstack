package com.joyeria.gestion_envio.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialResponseDTO {
    private Long idHistorial;
    private Long IdEnvio;
    private LocalDateTime fecha;
    private String estado;
    private Long usuaioId;
}
