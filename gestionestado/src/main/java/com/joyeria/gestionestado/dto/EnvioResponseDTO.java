package com.joyeria.gestionestado.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponseDTO {
    
    private Long idEnvio;
    private LocalDate fEnvio;
    private LocalDate fRecep;
    private Long idEstado;

}
