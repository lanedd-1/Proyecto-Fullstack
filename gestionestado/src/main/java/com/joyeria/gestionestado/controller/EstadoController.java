package com.joyeria.gestionestado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyeria.gestionestado.Service.EstadoService;
import com.joyeria.gestionestado.dto.EstadoConEnviosResponseDTO;
import com.joyeria.gestionestado.dto.EstadoRequestDTO;
import com.joyeria.gestionestado.dto.EstadoResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    private final EstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<EstadoResponseDTO>> getAll() {
        return ResponseEntity.ok(estadoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoResponseDTO> getById(@PathVariable Long id) {
        EstadoResponseDTO dto = estadoService.obtenerPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/con-envios")
    public ResponseEntity<EstadoConEnviosResponseDTO> getByIdConEnvios(@PathVariable Long id) {
        EstadoConEnviosResponseDTO dto = estadoService.obtenerConEnvios(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<EstadoResponseDTO> create(@Valid @RequestBody EstadoRequestDTO req) {
        EstadoResponseDTO created = estadoService.crear(req);
        return ResponseEntity.created(URI.create("/api/estados/" + created.getIdEstado())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EstadoRequestDTO req) {
        EstadoResponseDTO updated = estadoService.actualizar(id, req);
        return ResponseEntity.ok(updated);
    }


}
