package com.semestral.gestion_direccion.dto;

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
public class DireccionRequestDTO {
    @NotBlank(message = "La calle no puede estar vacia")
    @Schema(description = "Nombre de la calle", example = "Avenida Siempre Viva")
    private String calle;
    @NotBlank(message = "El numero no puede estar vacio")
    @Schema(description = "Numero de la Calle registrada", example = "123")
    private String numero;
    @NotNull(message = "El id no puede ser nula")
    @Positive(message = "el id debe ser mayor a 0")
    @Schema(description = "ID de la comuna asociada", example = "1")
    private Long idComuna;
    @NotNull(message = "El id no puede ser nula")
    @Positive(message = "el id debe ser mayor a 0")
    @Schema(description = "ID del usuario asociada", example = "1")
    private Long idUsuario;
    @NotNull(message = "El id no puede ser nula")
    @Positive(message = "el id debe ser mayor a 0")
    @Schema(description = "ID del estado asociado", example = "1")
    private Long idEstado;

}
