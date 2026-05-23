package com.joyeria.gestion_envio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialRequestDTO {
    @NotNull(message = "El ID del envío es obligatorio")
    @Positive(message = "El ID del envío debe ser un número positivo")
    private Long idEnvio;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long idUsuario;
}