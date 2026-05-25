package com.joyeria.gestionestado.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponseDTO {

     private Long idEnvio;
    private String fechaEnvio;
    private String fechaRecepcion;
    private Long idVenta;
    private Long idDireccion;
    private String estado;

}
