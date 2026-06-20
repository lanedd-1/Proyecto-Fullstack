package com.joyeria.gestion_configuracion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ConfiguracionRequestDTO {
   @NotNull(message = "La longitud minima es obligatoria")
    @Min(value = 8, message = "La longitud minima debe ser al menos 8")
    @Schema(
        description = "Longitud mínima permitida para la contraseña",
        example = "8",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer longitudMinima;

    @NotNull(message = "La longitud maxima es obligatoria")
    @Max(value = 128, message = "La longitud maxima no puede superar 128")
    @Schema(
        description = "Longitud máxima permitida para la contraseña",
        example = "20",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer longitudMaxima;

    @NotNull(message = "Debe indicar si se requiere mayuscula")
    @Schema(
        description = "Indica si la contraseña debe contener al menos una letra mayúscula",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean requiereMayuscula;

    @NotNull(message = "Debe indicar si se requiere minuscula")
    @Schema(
        description = "Indica si la contraseña debe contener al menos una letra minúscula",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean requiereMinuscula;

    @NotNull(message = "Debe indicar si se requiere numero")
    @Schema(
        description = "Indica si la contraseña debe contener al menos un número",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean requiereNumero;

    @NotNull(message = "Debe indicar si se requiere caracter especial")
    @Schema(
        description = "Indica si la contraseña debe contener al menos un carácter especial",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean requiereCaracterEspecial;

    @Size(max = 100, message = "Los caracteres especiales no pueden superar 100 caracteres")
    @Schema(
        description = "Lista de caracteres especiales permitidos en la contraseña",
        example = "!@#$%&*"
    )
    private String caracteresEspecialesPermitidos;

}
