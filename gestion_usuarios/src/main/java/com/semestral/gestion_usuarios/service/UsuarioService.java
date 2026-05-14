package com.semestral.gestion_usuarios.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.model.Usuario;
import com.semestral.gestion_usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRep;

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
    public List<UsuarioResponseDTO> getAllUsuarios(){
        return  usuarioRep.findAll().stream()
        .map(this::convertToDto).collect(Collectors.toList());
    }
    public Optional<UsuarioResponseDTO> encontrarPorId(Long id){
        return usuarioRep.findById(id).map(this::convertToDto);
    }
    public Optional<UsuarioResponseDTO> encontrarPorCorreo(String correo){
        return usuarioRep.findByCorreoU(correo);
    }
    public UsuarioResponseDTO saveUsuario(UsuarioRequestDTO usuario){
        Usuario us= new Usuario(null,usuario.getNombreU(),usuario.getClave(),usuario.getCorreoU(),usuario.getClave(),null);
        return convertToDto(usuarioRep.save(us));
    }
    public void eliminarUsuario(Long id){
        usuarioRep.deleteById(id);
    }

}
