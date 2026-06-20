package com.joyeria.gestion_configuracion.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class ConfiguracionResponseDTO {
    @Schema(
        description = "Longitud mínima permitida para la contraseña",
        example = "8"
    )
    private Integer longitudMinima;

    @Schema(
        description = "Longitud máxima permitida para la contraseña",
        example = "20"
    )
    private Integer longitudMaxima;

    @Schema(
        description = "Indica si la contraseña debe contener al menos una letra mayúscula",
        example = "true"
    )
    private Boolean requiereMayuscula;

    @Schema(
        description = "Indica si la contraseña debe contener al menos una letra minúscula",
        example = "true"
    )
    private Boolean requiereMinuscula;

    @Schema(
        description = "Indica si la contraseña debe contener al menos un número",
        example = "true"
    )
    private Boolean requiereNumero;

    @Schema(
        description = "Indica si la contraseña debe contener al menos un carácter especial",
        example = "true"
    )
    private Boolean requiereCaracterEspecial;

    @Schema(
        description = "Lista de caracteres especiales permitidos en la contraseña",
        example = "!@#$%&*"
    )
    private String caracteresEspecialesPermitidos;

    @Schema(
        description = "Lista de usuarios obtenida desde ms-usuario. " +
        "Viene vacía si ms-usuario no está disponible",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private List<UsuarioResponseDTO> usuarios;
}
