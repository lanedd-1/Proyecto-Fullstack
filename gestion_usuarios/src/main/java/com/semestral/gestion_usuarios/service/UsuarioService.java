package com.semestral.gestion_usuarios.service;
import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.exception.BusinessConflictException;
import com.semestral.gestion_usuarios.exception.ResourceNotFoundException;
import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.model.Usuario;
import com.semestral.gestion_usuarios.repository.RolRepository;
import com.semestral.gestion_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRep;
private final RolRepository rolRep;
private final BCryptPasswordEncoder passwordEncoder;

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
        nombreRol
    );
    }
@Transactional(readOnly = true)
public List<UsuarioResponseDTO> getAllUsuarios() {
    return usuarioRep.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
}

@Transactional(readOnly = true)
public UsuarioResponseDTO findByIdOrThrow(Long id) {
    Usuario u = usuarioRep.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
    return convertToDto(u);
}

@Transactional
public UsuarioResponseDTO saveUsuario(UsuarioRequestDTO usuario) {
    if (usuario.getIdRol() == null) {
        throw new BusinessConflictException("El id de rol es obligatorio");
    }

    Rol rol = rolRep.findById(usuario.getIdRol())
            .orElseThrow(() -> new ResourceNotFoundException(usuario.getIdRol()));

    if (usuarioRep.findByCorreoU(usuario.getCorreoU()).isPresent()) {
        throw new DataIntegrityViolationException("Correo ya registrado: " + usuario.getCorreoU());
    }

    Usuario us = new Usuario();
    us.setIdUsuario(null);
    us.setNombreU(usuario.getNombreU());
    us.setRutU(usuario.getRut());
    us.setCorreoU(usuario.getCorreoU());
    us.setClaveU(passwordEncoder != null ? passwordEncoder.encode(usuario.getClave()) : usuario.getClave());
    us.setRol(rol);

    Usuario guardado = usuarioRep.save(us);
    return convertToDto(guardado);
}
@Transactional
public UsuarioResponseDTO update(Long id, UsuarioRequestDTO usuario) {
    Usuario existing = usuarioRep.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException(id));
    if (usuario.getCorreoU() != null && !usuario.getCorreoU().equalsIgnoreCase(existing.getCorreoU())) {
        usuarioRep.findByCorreoU(usuario.getCorreoU()).ifPresent(u -> {
            throw new DataIntegrityViolationException("Correo ya registrado: " + usuario.getCorreoU());
        });
        existing.setCorreoU(usuario.getCorreoU());
    }

    if (usuario.getNombreU() != null) existing.setNombreU(usuario.getNombreU());
    if (usuario.getRut() != null) existing.setRutU(usuario.getRut());

    if (usuario.getClave() != null && !usuario.getClave().isBlank()) {
        existing.setClaveU(passwordEncoder != null ? passwordEncoder.encode(usuario.getClave()) : usuario.getClave());
    }

    if (usuario.getIdRol() != null) {
        Rol rol = rolRep.findById(usuario.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException(usuario.getIdRol()));
        existing.setRol(rol);
    }

    Usuario saved = usuarioRep.save(existing);
    return convertToDto(saved);
}
}