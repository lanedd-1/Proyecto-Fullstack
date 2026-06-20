package com.semestral.venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {

    @Schema(
        description = "Fecha de la venta en formato YYYY-MM-DD",
        example = "2024-06-15T14:30:00"
    )
    @NotBlank(message = "La fecha de venta no puede estar vacia")
    private String fechaV;
}
