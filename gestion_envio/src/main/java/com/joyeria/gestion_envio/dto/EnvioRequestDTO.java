package com.joyeria.gestion_envio.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para solicitar la creación o actualización de un envío")
public class EnvioRequestDTO {

    @NotNull(message = "La fecha de envío es obligatoria")
    @PastOrPresent(message = "La fecha de envío no puede ser futura")
    @Schema(description = "Fecha del envío", example = "2026-06-17T10:00:00")
    private LocalDateTime fechaEnvio;

    @NotNull(message = "La fecha de recepción es obligatoria")
    @Schema(description = "Fecha de recepción estimada", example = "2026-06-20T14:00:00")
    private LocalDateTime fechaRecepcion; 

    @NotNull(message = "El ID de la venta es obligatorio")
    @Positive(message = "El ID de la venta debe ser un número positivo")
    @Schema(description = "ID de la venta a enviar", example = "50")
    private Long idVenta;

    @NotNull(message = "El ID de la dirección es obligatorio")
    @Positive(message = "El ID de la dirección debe ser un número positivo")
    @Schema(description = "ID de la dirección de destino", example = "10")
    private Long idDireccion;

    @NotBlank(message = "El estado no puede estar vacío")
    @Schema(description = "Estado inicial del envío", example = "PREPARACION")
    private String estado;
}
