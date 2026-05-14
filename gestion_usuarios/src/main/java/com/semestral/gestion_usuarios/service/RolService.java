package com.semestral.gestion_usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {
    private final RolRepository rolRep;
    public Rol getById(Long id) {
        return rolRep.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    public List<Rol> findAll() {
        return rolRep.findAll();
    }

    public Optional<Rol> findByNombre(String nombre) {
        return rolRep.findByNombreRol(nombre);
    }
    public Optional<Rol> findByNombreIgnoreCase(String nombre) {
    return rolRep.findByNombreRolIgnoreCase(nombre);
    }
    public Rol create(Rol rol) {
    // evita duplicados por nombre (case-insensitive)
    rolRep.findByNombreRolIgnoreCase(rol.getNombreRol())
        .ifPresent(r -> { throw new DataIntegrityViolationException("Rol ya existe: " + rol.getNombreRol()); });
    return rolRep.save(rol);

    }
    public void borrar(Long id){
        rolRep.deleteById(id);
    }
    public Rol update(Long id, Rol rol) {
    Rol existing = rolRep.findById(id)
        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + id));
    String nuevoNombre = rol.getNombreRol();
    if (nuevoNombre != null && !nuevoNombre.equalsIgnoreCase(existing.getNombreRol())) {
        rolRep.findByNombreRolIgnoreCase(nuevoNombre)
            .ifPresent(r -> { throw new DataIntegrityViolationException("Otro rol con ese nombre ya existe: " + nuevoNombre); });
        existing.setNombreRol(nuevoNombre);
    }
    return rolRep.save(existing);
    }
}
