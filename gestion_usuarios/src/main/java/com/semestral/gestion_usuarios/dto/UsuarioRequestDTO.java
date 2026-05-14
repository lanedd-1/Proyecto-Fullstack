package com.semestral.gestion_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank
    
    private String nombreU;
    @NotBlank
    private String rut;
    @NotBlank
    @Email
    private String correoU;
    @NotBlank
    private String clave;
    @Positive
    private Long idRol;

}
