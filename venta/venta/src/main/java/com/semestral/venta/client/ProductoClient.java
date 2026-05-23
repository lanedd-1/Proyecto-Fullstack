package com.semestral.venta.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "producto-service", url = "${productos.service.url}")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    Map<String, Object> obtenerPorId(@PathVariable("id") Long id);

}
