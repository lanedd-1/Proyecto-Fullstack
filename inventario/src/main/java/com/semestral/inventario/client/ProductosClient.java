package com.semestral.inventario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.semestral.inventario.dto.ProductoDTO;

@FeignClient(name = "ms-productos", url = "${ms.productos.url}")
public interface ProductosClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable Long id);
}
