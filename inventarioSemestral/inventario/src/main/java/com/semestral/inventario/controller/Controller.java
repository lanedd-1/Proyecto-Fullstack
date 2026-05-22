package com.semestral.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class Controller {

    private final InventarioService invService;

    @PostMapping("/agregar")
    public ResponseEntity<InventarioResponseDTO> agregarStock(@RequestBody InventarioRequestDTO request) {
        return ResponseEntity.ok(invService.agregarStock(request));
    }

    @PostMapping("/descontar")
    public ResponseEntity<InventarioResponseDTO> descontarStock(@RequestBody InventarioRequestDTO request) {
        return ResponseEntity.ok(invService.descontarStock(request));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerStockPorProducto(
            @PathVariable("idProducto") Long idProducto) {
        return ResponseEntity.ok(invService.getStockPorProducto(idProducto));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodoElStock() {
        return ResponseEntity.ok(invService.getTodoStock());
    }
}

