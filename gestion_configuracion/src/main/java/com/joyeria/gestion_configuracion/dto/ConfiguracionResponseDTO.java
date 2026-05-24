package com.joyeria.gestion_configuracion.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class ConfiguracionResponseDTO {
    private Integer longitudMinima;
    private Integer longitudMaxima;
    private Boolean requiereMayuscula;
    private Boolean requiereMinuscula;
    private Boolean requiereNumero;
    private Boolean requiereCaracterEspecial;
    private String caracteresEspecialesPermitidos;

    private List<UsuarioResponseDTO> usuarios;
}
