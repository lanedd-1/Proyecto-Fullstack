package com.joyeria.gestionestado.exception;

public class EstadoDuplicadoException extends RuntimeException{

    private final String nombreEstado;

    public EstadoDuplicadoException(String nombre){
        super("Ya existe un estado con el nombre: '" + nombre + "'");
        this.nombreEstado = nombre;
    }

    public String getNombreEstado(){
        return nombreEstado;
    }

}
