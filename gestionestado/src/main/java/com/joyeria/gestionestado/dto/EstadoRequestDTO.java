package com.joyeria.gestionestado.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoRequestDTO {

    @Schema(
        description = "Id único del estado, generado automáticamente",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "El mnombre del estado es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(
        description = "Nombre del Estado",
        example = "Inactivo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombreEstado;

}
