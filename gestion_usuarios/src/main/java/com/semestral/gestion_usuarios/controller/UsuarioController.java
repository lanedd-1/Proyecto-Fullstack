package com.semestral.gestion_usuarios.controller;
import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

@GetMapping
public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
    return ResponseEntity.ok(usuarioService.getAllUsuarios());
}

@GetMapping("/{id}")
public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
    UsuarioResponseDTO dto = usuarioService.findByIdOrThrow(id);
    return ResponseEntity.ok(dto);
}

@PostMapping
public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO req) {
    UsuarioResponseDTO created = usuarioService.saveUsuario(req);
    return ResponseEntity.created(URI.create("/api/usuarios/" + created.getId())).body(created);
}

@PutMapping("/{id}")
public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO req) {
    UsuarioResponseDTO updated = usuarioService.update(id, req);
    return ResponseEntity.ok(updated);
}
}
