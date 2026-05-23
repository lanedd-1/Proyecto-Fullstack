package com.semestral.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.inventario.dto.EstanteRequestDTO;
import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.dto.PasilloRequestDTO;
import com.semestral.inventario.dto.UbicacionRequestDTO;
import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;
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

    @PostMapping("/pasillos")
    public ResponseEntity<Pasillo> crearPasillo(@RequestBody PasilloRequestDTO request) {
        return ResponseEntity.ok(invService.crearPasillo(request));
    }

    @PostMapping("/estantes")
    public ResponseEntity<Estante> crearEstante(@RequestBody EstanteRequestDTO request) {
        return ResponseEntity.ok(invService.crearEstante(request));
    }

    @PostMapping("/ubicaciones")
    public ResponseEntity<Ubicacion> crearUbicacion(@RequestBody UbicacionRequestDTO request) {
        return ResponseEntity.ok(invService.crearUbicacion(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerStockPorProducto(
            @PathVariable("idProducto") Long idProducto) {
        return ResponseEntity.ok(invService.getStockPorProducto(idProducto));
    }

    @GetMapping()
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodoElStock() {
        return ResponseEntity.ok(invService.getTodoStock());
    }
}

