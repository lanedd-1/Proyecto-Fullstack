package com.joyeria.gestionestado.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoRequestDTO {

    @NotBlank(message = "El nombre del estado es obligatorio")
    private String nombreEstado;

}
