package com.joyeria.gestion_configuracion.exception;

public class ConfiguracionNotFoundException extends RuntimeException {

    public ConfiguracionNotFoundException() {
        super("No existe configuracion de contrasena en el sistema");
    }
}
