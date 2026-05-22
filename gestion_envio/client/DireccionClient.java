package com.joyeria.gestion_envio.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign leerá el puerto 8081 que configuraste en el application.properties
@FeignClient(name = "gestion-direcciones", url = "${ms.direccion.url}/api/direcciones")
public interface DireccionClient {

    @GetMapping("/{id}")
    Object obtenerDireccionPorId(@PathVariable("id") Long id);
}
