package com.semestral.gestion_usuarios.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gestion-estado", url = "${ms.estado.url}/api/estados")
public interface EstadoClient {

    @GetMapping("/{id}")
    Object obtenerEstadoPorId(@PathVariable("id") Long id);
}