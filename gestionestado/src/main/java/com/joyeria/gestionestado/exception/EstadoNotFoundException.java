package com.joyeria.gestionestado.exception;

public class EstadoNotFoundException extends RuntimeException{
     private final Long estadoId;

    public EstadoNotFoundException(Long id){
        super("Estado no encontrado con ID: "+ id);
        this.estadoId = id;
    }

    public Long getEstadoId(){
        return estadoId;
    }
}
