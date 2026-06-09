package com.semestral.productos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.dto.ProductoResponseDTO;
import com.semestral.productos.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.semestral.productos.exception.ResourceNotFoundException;


@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService prodService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> encontrarPorId(@PathVariable Long id){
        return prodService.encontrarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping()
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductos() {
        return ResponseEntity.ok(prodService.getAllProductos());
    }


    @PostMapping("/agregar")
    public ResponseEntity<ProductoResponseDTO>agregarProd(@Valid @RequestBody ProductoRequestDTO prod){
        return ResponseEntity.status(201).body(prodService.saveProducto(prod));
        
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        if (prodService.encontrarPorId(id).isEmpty()) {
            throw new ResourceNotFoundException(id);
        }
        prodService.eliminarProd(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<ProductoResponseDTO> actualizarPrecio(@PathVariable Long id, @RequestBody BigDecimal precio){
        ProductoResponseDTO updated = prodService.updatePrecio(id, precio);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/descripcion")
    public ResponseEntity<ProductoResponseDTO> actualizarDescripcion(@PathVariable Long id, @RequestBody ProductoRequestDTO body){
        ProductoResponseDTO updated = prodService.updateDescripcion(id, body.getDescProd());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductoResponseDTO> actualizarStock(@PathVariable Long id, @RequestBody Long stock){
        ProductoResponseDTO updated = prodService.updateStock(id, stock);
        return ResponseEntity.ok(updated);
    }
    
}
