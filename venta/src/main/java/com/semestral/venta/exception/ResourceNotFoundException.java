package com.semestral.venta.exception;

public class ResourceNotFoundException extends RuntimeException{

    
    private final Long resourceId;
    public ResourceNotFoundException(Long id) {
        super("Recurso no encontrado con ID: " + id);
        this.resourceId = id;
    }
    public Long getResourceId() { return resourceId; }
}
