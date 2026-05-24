package com.joyeria.gestion_configuracion.exception;

public class ConfiguracionLongitudInvalidaException extends RuntimeException {

    public ConfiguracionLongitudInvalidaException(int min, int max) {
        super(String.format("La longitud minima (%d) no puede ser mayor o igual a la longitud maxima (%d)", min, max));
    }

}
