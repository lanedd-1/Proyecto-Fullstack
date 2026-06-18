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
@Table(name = "envio")
@Schema(description = "Entidad que representa el proceso de envío de una venta realizada")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del envío", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idEnvio;

    @Schema(description = "Fecha en la que se realizó el envío", example = "2026-06-17T10:00:00")
    private LocalDateTime fechaEnvio;

    @Schema(description = "Fecha estimada o real de recepción del envío", example = "2026-06-20T14:00:00")
    private LocalDateTime fechaRecep;

    @Schema(description = "ID de la venta asociada al envío", example = "50")
    private Long idVenta;

    @Schema(description = "ID de la dirección de destino", example = "10")
    private Long idDireccion;

    @Schema(description = "Estado actual del envío", example = "EN_TRANSITO")
    private String estado;
}