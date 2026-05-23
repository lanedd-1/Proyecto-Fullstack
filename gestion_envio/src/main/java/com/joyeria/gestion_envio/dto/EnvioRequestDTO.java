package com.joyeria.gestion_envio.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvioRequestDTO {

    @NotNull(message = "La fecha de envío es obligatoria")
    @PastOrPresent(message = "La fecha de envío no puede ser futura")
    private LocalDateTime fechaEnvio;

    @NotNull(message = "La fecha de recepción es obligatoria")
    // Nota: Validar que sea posterior a fechaEnvio requiere lógica personalizada o @AssertTrue
    private LocalDateTime fechaRecepcion; 

    @NotNull(message = "El ID de la venta es obligatorio")
    @Positive(message = "El ID de la venta debe ser un número positivo")
    private Long idVenta;

    @NotNull(message = "El ID de la dirección es obligatorio")
    @Positive(message = "El ID de la dirección debe ser un número positivo")
    private Long idDireccion;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
}
