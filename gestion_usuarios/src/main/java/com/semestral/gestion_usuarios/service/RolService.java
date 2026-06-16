package com.semestral.gestion_usuarios.service;

import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j 
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRep;

    public List<Rol> findAll() {
        log.info("[RolService] Consultando todos los roles registrados");
        return rolRep.findAll();
    }

    public Rol getById(Long id) {
        log.info("[RolService] Buscando rol con ID: {}", id);
        
        return rolRep.findById(id)
                .orElseThrow(() -> {
                    log.warn("[RolService] Rol ID {} no encontrado en la base de datos", id);
                    return new RuntimeException("Rol no encontrado: " + id); 
                });
    }

    public Optional<Rol> findByNombre(String nombre) {
        log.debug("[RolService] Buscando rol por nombre exacto: {}", nombre);
        return rolRep.findByNombreRol(nombre);
    }

    public Optional<Rol> findByNombreIgnoreCase(String nombre) {
        log.debug("[RolService] Buscando rol por nombre (ignorando mayúsculas/minúsculas): {}", nombre);
        return rolRep.findByNombreRolIgnoreCase(nombre);
    }

    public Rol create(Rol rol) {
        log.info("[RolService] Intentando crear nuevo rol: {}", rol.getNombreRol());
        
        // Evita duplicados por nombre (case-insensitive)
        rolRep.findByNombreRolIgnoreCase(rol.getNombreRol())
                .ifPresent(r -> {
                    log.warn("[RolService] Creación fallida: El rol '{}' ya existe", rol.getNombreRol());
                    throw new DataIntegrityViolationException("Rol ya existe: " + rol.getNombreRol());
                });
                
        Rol guardado = rolRep.save(rol);
        log.info("[RolService] Rol '{}' creado exitosamente con ID: {}", guardado.getNombreRol(), guardado.getIdRol());
        return guardado;
    }

    public Rol update(Long id, Rol rol) {
        log.info("[RolService] Iniciando actualización para el rol ID: {}", id);
        
        Rol existing = rolRep.findById(id)
                .orElseThrow(() -> {
                    log.warn("[RolService] Fallo al actualizar: Rol ID {} no encontrado", id);
                    return new RuntimeException("Rol no encontrado: " + id);
                });
                
        String nuevoNombre = rol.getNombreRol();
        if (nuevoNombre != null && !nuevoNombre.equalsIgnoreCase(existing.getNombreRol())) {
            rolRep.findByNombreRolIgnoreCase(nuevoNombre)
                    .ifPresent(r -> {
                        log.warn("[RolService] Choque de nombres: ya existe otro rol llamado '{}'", nuevoNombre);
                        throw new DataIntegrityViolationException("Otro rol con ese nombre ya existe: " + nuevoNombre);
                    });
            existing.setNombreRol(nuevoNombre);
        }
        
        Rol actualizado = rolRep.save(existing);
        log.info("[RolService] Rol ID {} actualizado correctamente a '{}'", id, actualizado.getNombreRol());
        return actualizado;
    }
}