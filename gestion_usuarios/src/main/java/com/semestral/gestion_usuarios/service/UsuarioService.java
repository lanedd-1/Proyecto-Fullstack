package com.semestral.gestion_usuarios.service;

import com.semestral.gestion_usuarios.client.EstadoClient;
import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.exception.BusinessConflictException;
import com.semestral.gestion_usuarios.exception.ExternalServiceException;
import com.semestral.gestion_usuarios.exception.ResourceNotFoundException;
import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.model.Usuario;
import com.semestral.gestion_usuarios.repository.RolRepository;
import com.semestral.gestion_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRep;
    private final RolRepository rolRep;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EstadoClient estadoClient;

    private UsuarioResponseDTO convertToDto(Usuario u) {
        if (u == null) return null;
        Long idRol = u.getRol() != null ? u.getRol().getIdRol() : null;
        String nombreRol = u.getRol() != null ? u.getRol().getNombreRol() : null;
        return new UsuarioResponseDTO(
            u.getIdUsuario(),  
            u.getNombreU(),
            u.getRutU(),
            u.getCorreoU(),
            idRol,
            nombreRol,
            u.getIdEstado()
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getAllUsuarios() {
        log.info("[UsuarioService] Consultando todos los usuarios registrados");
        
        return usuarioRep.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByIdOrThrow(Long id) {
        log.info("[UsuarioService] Buscando usuario con ID: {}", id);
        
        Usuario u = usuarioRep.findById(id)
                .orElseThrow(() -> {
                    log.warn("[UsuarioService] Usuario ID {} no encontrado en la base de datos", id);
                    return new ResourceNotFoundException(id);
                });
                
        return convertToDto(u);
    }

    @Transactional
    public UsuarioResponseDTO saveUsuario(UsuarioRequestDTO usuario) {
        log.info("[UsuarioService] Iniciando creación de usuario con correo: {}", usuario.getCorreoU());

        if (usuario.getIdRol() == null) {
            log.warn("[UsuarioService] Fallo al crear usuario: ID de rol es nulo");
            throw new BusinessConflictException("El id de rol es obligatorio");
        }
        if (usuario.getIdEstado() == null) {
            log.warn("[UsuarioService] Fallo al crear usuario: ID de estado es nulo");
            throw new BusinessConflictException("El id de estado es obligatorio");
        }

        Rol rol = rolRep.findById(usuario.getIdRol())
                .orElseThrow(() -> {
                    log.warn("[UsuarioService] Rol ID {} no encontrado al intentar crear usuario", usuario.getIdRol());
                    return new ResourceNotFoundException(usuario.getIdRol());
                });

        try {
            estadoClient.obtenerEstadoPorId(usuario.getIdEstado());
        } catch (feign.FeignException.NotFound e) {
            log.warn("[UsuarioService] Estado ID {} no encontrado en el microservicio externo (ms-estado)", usuario.getIdEstado());
            throw new ResourceNotFoundException(usuario.getIdEstado());
        } catch (feign.FeignException e) {
            // AQUÍ LANZAMOS LA NUEVA EXCEPCIÓN 503
            log.error("[UsuarioService] Error al conectar con ms-estados: {}", e.getMessage());
            throw new ExternalServiceException("El microservicio de Estados no se encuentra disponible en este momento.");
        }

        if (usuarioRep.findByCorreoU(usuario.getCorreoU()).isPresent()) {
            log.warn("[UsuarioService] Intento de registro con correo duplicado: {}", usuario.getCorreoU());
            throw new DataIntegrityViolationException("Correo ya registrado: " + usuario.getCorreoU());
        }

        Usuario us = new Usuario();
        us.setIdUsuario(null);
        us.setNombreU(usuario.getNombreU());
        us.setRutU(usuario.getRut());
        us.setCorreoU(usuario.getCorreoU());
        us.setClaveU(passwordEncoder != null ? passwordEncoder.encode(usuario.getClave()) : usuario.getClave());
        us.setRol(rol);
        us.setIdEstado(usuario.getIdEstado());

        Usuario guardado = usuarioRep.save(us);
        log.info("[UsuarioService] Usuario creado exitosamente con ID: {}", guardado.getIdUsuario());
        
        return convertToDto(guardado);
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponseDTO loginDirecto(String correo, String clave) {
        log.info("[UsuarioService] Intentando iniciar sesión para el correo: {}", correo);

        if (correo == null || clave == null || correo.isBlank() || clave.isBlank()) {
            log.warn("[UsuarioService] Fallo de login: Credenciales vacías o nulas");
            throw new BusinessConflictException("El correo y la contraseña son obligatorios");
        }
        Usuario usuario = usuarioRep.findByCorreoU(correo)
            .orElseThrow(() -> {
                log.warn("[UsuarioService] Fallo de login: El correo '{}' no existe", correo);
                return new BusinessConflictException("Credenciales inválidas: El correo o la contraseña son incorrectos");
            });
        boolean passwordMatches = passwordEncoder != null ? passwordEncoder.matches(clave, usuario.getClaveU()) : clave.equals(usuario.getClaveU());
        if (!passwordMatches) {
            log.warn("[UsuarioService] Fallo de login: Contraseña incorrecta para el correo '{}'", correo);
            throw new BusinessConflictException("Credenciales inválidas: El correo o la contraseña son incorrectos");
        }

        log.info("[UsuarioService] Login exitoso para el usuario ID: {}", usuario.getIdUsuario());
        return convertToDto(usuario);
    }

    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO usuario) {
        log.info("[UsuarioService] Iniciando actualización para el usuario ID: {}", id);

        Usuario existing = usuarioRep.findById(id)
        .orElseThrow(() -> {
            log.warn("[UsuarioService] Fallo al actualizar: Usuario ID {} no encontrado", id);
            return new ResourceNotFoundException(id);
        });
        
        if (usuario.getCorreoU() != null && !usuario.getCorreoU().equalsIgnoreCase(existing.getCorreoU())) {
            usuarioRep.findByCorreoU(usuario.getCorreoU()).ifPresent(u -> {
                log.warn("[UsuarioService] Choque de correos en actualización: el correo {} ya pertenece a otro usuario", usuario.getCorreoU());
                throw new DataIntegrityViolationException("Correo ya registrado: " + usuario.getCorreoU());
            });
            existing.setCorreoU(usuario.getCorreoU());
        }

        if (usuario.getNombreU() != null) existing.setNombreU(usuario.getNombreU());
        if (usuario.getRut() != null) existing.setRutU(usuario.getRut());

        if (usuario.getClave() != null && !usuario.getClave().isBlank()) {
            log.debug("[UsuarioService] Actualizando contraseña para el usuario ID: {}", id);
            existing.setClaveU(passwordEncoder != null ? passwordEncoder.encode(usuario.getClave()) : usuario.getClave());
        }

        if (usuario.getIdRol() != null) {
            Rol rol = rolRep.findById(usuario.getIdRol())
                    .orElseThrow(() -> {
                        log.warn("[UsuarioService] Fallo al actualizar rol: Rol ID {} no encontrado", usuario.getIdRol());
                        return new ResourceNotFoundException(usuario.getIdRol());
                    });
            existing.setRol(rol);
        }

        if (usuario.getIdEstado() != null) {
            try {
                estadoClient.obtenerEstadoPorId(usuario.getIdEstado());
            } catch (feign.FeignException.NotFound e) {
                log.warn("[UsuarioService] Fallo al actualizar estado: Estado ID {} no existe en ms-estado", usuario.getIdEstado());
                throw new ResourceNotFoundException(usuario.getIdEstado());
            } catch (feign.FeignException e) {
                // AQUÍ LANZAMOS LA NUEVA EXCEPCIÓN 503
                log.error("[UsuarioService] Error al conectar con ms-estados: {}", e.getMessage());
                throw new ExternalServiceException("El microservicio de Estados no se encuentra disponible en este momento.");
            }
            existing.setIdEstado(usuario.getIdEstado());
        }

        Usuario saved = usuarioRep.save(existing);
        log.info("[UsuarioService] Usuario ID {} actualizado correctamente", saved.getIdUsuario());
        
        return convertToDto(saved);
    }
}