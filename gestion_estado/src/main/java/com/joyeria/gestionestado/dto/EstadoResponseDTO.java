package com.joyeria.gestionestado.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoResponseDTO {
    
    @Schema(
        description = "ID único del estado, generado automáticamente",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idEstado;

    @Schema(
        description = "Nombre del estado de envío",
        example = "En Camino"
    )
    private String nombreEstado;

}
