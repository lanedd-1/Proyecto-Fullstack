package com.semestral.gestion_direccion.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.service.DireccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("api/direcciones")
public class DireccionController {
    
    private final DireccionService direccionService;
    //Metodo GET por id 
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(direccionService.obtenerPorId(id));
    }
    //Metodo POST
    @PostMapping("/guardar")
    public ResponseEntity <DireccionResponseDTO> crear(@Valid @RequestBody DireccionRequestDTO dir){
        return ResponseEntity.status(201).body(direccionService.guardar(dir));
    }
    @GetMapping()
    public ResponseEntity<List<DireccionResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(direccionService.obtenerTodas());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {
        direccionService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

