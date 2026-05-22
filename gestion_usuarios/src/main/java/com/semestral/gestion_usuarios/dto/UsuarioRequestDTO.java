package com.semestral.gestion_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "el nombre no puede estar vacio")
    private String nombreU;
    @NotBlank(message = "el rut no puede estar vacio")
    private String rut;
    @NotBlank(message = "el correo no puede estar vacio")
    @Email
    private String correoU;
    @NotBlank(message = "la clave no puede estar vacia")
    private String clave;
    @Positive(message = "el id del rol debe ser número mayor a cero")
    private Long idRol;
    @NotNull(message = "el id de estado no puede ser nulo")
    @Positive(message = "el id de estado debe ser un número mayor a cero")
    private Long idEstado;
}
