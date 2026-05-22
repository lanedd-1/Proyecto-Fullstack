package com.joyeria.gestion_envio.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign leerá el puerto 8090 que configuraste en el application.properties
@FeignClient(name = "gestion-ventas", url = "${ms.venta.url}/api/ventas")
public interface VentaClient {

    @GetMapping("/{id}")
    Object obtenerVentaPorId(@PathVariable("id") Long id);
}