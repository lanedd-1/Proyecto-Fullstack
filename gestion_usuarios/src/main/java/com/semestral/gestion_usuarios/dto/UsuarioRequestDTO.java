package com.semestral.gestion_usuarios.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto con los datos requeridos para crear o actualizar un usuario")
public class UsuarioRequestDTO {

    @NotBlank(message = "el nombre no puede estar vacio")
    @Schema(description = "Nombre completo del usuario", example = "José Recabarren")
    private String nombreU;

    @NotBlank(message = "el rut no puede estar vacio")
    @Schema(description = "RUT del usuario sin puntos y con guion", example = "19234567-8")
    private String rut;

    @NotBlank(message = "el correo no puede estar vacio")
    @Email
    @Schema(description = "Correo electrónico de contacto", example = "user@example.com")
    private String correoU;

    @NotBlank(message = "la clave no puede estar vacía")
    @Size(min = 8, max = 20, message = "La clave debe tener entre 8 y 20 caracteres")
    @Schema(description = "Contraseña en texto plano (será encriptada por el backend)", example = "MiClaveSegura123")
    private String clave;

    @Positive(message = "el id del rol debe ser número mayor a cero")
    @Schema(description = "ID del Rol asociado", example = "2")
    private Long idRol;

    @NotNull(message = "el id de estado no puede ser nulo")
    @Positive(message = "el id de estado debe ser un número mayor a cero")
    @Schema(description = "ID del estado (validado mediante Feign con el ms-estado)", example = "1")
    private Long idEstado;
}