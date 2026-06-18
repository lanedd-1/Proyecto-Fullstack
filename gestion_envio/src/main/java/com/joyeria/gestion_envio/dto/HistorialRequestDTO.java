package com.joyeria.gestion_envio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para registrar un nuevo movimiento en el historial de un envío")
public class HistorialRequestDTO {
    @NotNull(message = "El ID del envío es obligatorio")
    @Positive(message = "El ID del envío debe ser un número positivo")
    @Schema(description = "ID del envío al que pertenece este movimiento", example = "1")
    private Long idEnvio;

    @NotBlank(message = "El estado no puede estar vacío")
    @Schema(description = "Nuevo estado asignado al envío", example = "ENTREGADO")
    private String estado;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    @Schema(description = "ID del usuario responsable de realizar el cambio", example = "5")
    private Long idUsuario;
}