package com.joyeria.gestionestado.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoConEnviosResponseDTO {

    private Long idEstado;
    private String nombreEstado;

    private List<EnvioResponseDTO> envios;

}
