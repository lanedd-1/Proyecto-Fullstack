package com.semestral.gestion_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos para las respuestas de Usuario")
public class UsuarioResponseDTO {

    @Schema(description = "Identificador único autogenerado", example = "1")
    private Long idU;

    @Schema(description = "Nombre completo del usuario", example = "José Recabarren")
    private String nombreU;

    @Schema(description = "RUT del usuario sin puntos y con guión", example = "12345678-9")
    private String rut;

    @Schema(description = "Correo electrónico institucional", example = "admin@JoyeriaEter.com")
    private String correoU;

    @Schema(description = "ID del Rol asignado", example = "1")
    private Long idRol;

    @Schema(description = "Nombre del Rol asignado", example = "ADMIN")
    private String nombreRol;

    @Schema(description = "ID del estado actual del usuario (validado con ms-estados)", example = "1")
    private Long idEstado;
}