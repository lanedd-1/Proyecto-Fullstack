package com.semestral.gestion_direccion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.model.Direccion;
import com.semestral.gestion_direccion.service.DireccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Controller
@RequestMapping("api/direcciones")
public class DireccionController {
    
    private final DireccionService direccionService;
    //Metodo GET por id 
    @GetMapping("/{id}")
    public ResponseEntity<Direccion> obtenerPorId(@PathVariable Long id){
        return direccionService.obtenerPorId(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }
    //Metodo POST
    @PostMapping("/guardar")
    public ResponseEntity <DireccionResponseDTO> crear(@Valid @RequestBody DireccionRequestDTO dir){
        return ResponseEntity.status(201).body(direccionService.guardar(dir));
    }
}
