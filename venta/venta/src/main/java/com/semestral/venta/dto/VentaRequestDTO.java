package com.semestral.venta.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {


    @NotBlank(message = "La fecha no puede estar vacia")private String fechaV;

    @Positive
    @NotNull(message = "El total no puede estar vacio")private Double total;
}
