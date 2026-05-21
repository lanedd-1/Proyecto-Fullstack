package com.semestral.gestion_direccion.controller;
import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {
private final DireccionService direccionService;

@GetMapping
public ResponseEntity<List<DireccionResponseDTO>> getAll() {
    return ResponseEntity.ok(direccionService.findAll());
}

@GetMapping("/{id}")
public ResponseEntity<DireccionResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(direccionService.findByIdOrThrow(id));
}

@GetMapping("/batch")
public ResponseEntity<List<DireccionResponseDTO>> getBatch(@RequestParam Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        return ResponseEntity.ok(List.of());
    }
    return ResponseEntity.ok(direccionService.findByIds(ids));
}

@PostMapping
public ResponseEntity<DireccionResponseDTO> create(@Valid @RequestBody DireccionRequestDTO req) {
    DireccionResponseDTO created = direccionService.create(req);
    return ResponseEntity.created(URI.create("/api/direcciones/" + created.getIdDireccion())).body(created);
}

@PutMapping("/{id}")
public ResponseEntity<DireccionResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody DireccionRequestDTO req) {
    DireccionResponseDTO updated = direccionService.update(id, req);
    return ResponseEntity.ok(updated);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    direccionService.delete(id);
    return ResponseEntity.noContent().build();
}
}