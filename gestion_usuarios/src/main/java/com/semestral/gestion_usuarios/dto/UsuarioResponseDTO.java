package com.semestral.gestion_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombreUser;
    private String rut;
    private String correoU;
    private Long idRol;
    private String nombreRol;
}
