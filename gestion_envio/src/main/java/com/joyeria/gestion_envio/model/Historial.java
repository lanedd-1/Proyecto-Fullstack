package com.joyeria.gestion_envio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "historial_envios")
@Schema(description = "Entidad que registra los cambios de estado de un envío")
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del registro de historial", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idHistorial;

    @Schema(description = "ID del envío relacionado", example = "1")
    private Long idEnvio;

    @Schema(description = "Fecha y hora del cambio de estado", example = "2026-06-17T12:00:00")
    private LocalDateTime fecha;

    @Schema(description = "Nuevo estado del envío", example = "ENTREGADO")
    private String estado;

    @Schema(description = "ID del usuario que realizó la actualización", example = "5")
    private Long idUsuario;
}