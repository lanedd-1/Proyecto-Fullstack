package com.semestral.venta.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventario-service", url = "${inventario.service.url}")
public interface InventarioClient {

    @GetMapping("/api/inventario/{id}")
    List<Map<String, Object>> obtenerStockPorProducto(@PathVariable("id") Long id);

    @PutMapping("/api/inventario/descontar/producto")
    Map<String, Object> descontarStockPorProducto(@RequestBody Map<String, Object> request);

}
