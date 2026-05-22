package com.semestral.gestion_direccion.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-estados", url = "${ms.estado.url}")
public interface EstadoClient {

    @GetMapping("/api/estados/{id}")
    Object obtenerEstadoPorId(@PathVariable("id") Long id);
}
