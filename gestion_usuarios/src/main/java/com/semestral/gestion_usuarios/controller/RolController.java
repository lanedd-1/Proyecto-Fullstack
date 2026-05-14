package com.semestral.gestion_usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.service.RolService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {
    private final RolService rolService;

    
@GetMapping
public ResponseEntity<List<Rol>> getAll() {
    return ResponseEntity.ok(rolService.findAll());
}

@GetMapping("/{id}")
public ResponseEntity<Rol> getById(@PathVariable Long id) {
    Rol rol = rolService.getById(id);
    return ResponseEntity.ok(rol);
}

@PutMapping("/{id}")
public ResponseEntity<Rol> update(@PathVariable Long id, @Valid @RequestBody Rol rol) {
    Rol updated = rolService.update(id, rol);
    return ResponseEntity.ok(updated);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    rolService.borrar(id);
    return ResponseEntity.noContent().build();
}
@PostMapping("/guardar")
public ResponseEntity<?> crearRol(@RequestBody Rol rol) {
    try {
        // Guardamos el nuevo rol (por ejemplo: { "nombreRol": "INVITADO" })
        Rol nuevoRol = rolService.create(rol);
        
        // Retornamos el rol creado con el ID que le asignó la base de datos
        return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el rol: " + e.getMessage());
    }
}
}

