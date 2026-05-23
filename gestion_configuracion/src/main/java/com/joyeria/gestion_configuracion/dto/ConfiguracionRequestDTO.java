package com.joyeria.gestion_configuracion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConfiguracionRequestDTO {
    @NotNull(message = "La longitud minima es obligatoria")
    @Min(value = 8, message = "La longitud minima debe ser al menos 8")
    private Integer longitudMinima;

    @NotNull(message = "La longitud maxima es obligatoria")
    @Max(value = 128, message = "La longitud maxima no puede superar 128")
    private Integer longitudMaxima;

    @NotNull(message = "Debe indicar si se requiere mayuscula")
    private Boolean requiereMayuscula;

    @NotNull(message = "Debe indicar si se requiere minuscula")
    private Boolean requiereMinuscula;

    @NotNull(message = "Debe indicar si se requiere numero")
    private Boolean requiereNumero;

    @NotNull(message = "Debe indicar si se requiere caracter especial")
    private Boolean requiereCaracterEspecial;

    @Size(max = 100, message = "Los caracteres especiales no pueden superar 100 caracteres")
    private String caracteresEspecialesPermitidos;
}
