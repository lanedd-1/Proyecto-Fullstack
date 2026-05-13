package com.semestral.productos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.productos.dto.CategoriaRequestDTO;
import com.semestral.productos.dto.CategoriaResponseDTO;
import com.semestral.productos.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService catServ;

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> obtenerCategorias() {
        return ResponseEntity.ok(catServ.findAllCat());
    }

    @PostMapping("/agregarCat")
    public ResponseEntity<CategoriaResponseDTO>agregarCat(@Valid @RequestBody CategoriaRequestDTO cat){
        return ResponseEntity.status(201).body(catServ.saveCat(cat));
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> encontrarPorId(@PathVariable Long idCat){
        return catServ.buscarPorId(idCat).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
