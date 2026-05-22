package com.semestral.venta.dto;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {


    private String fechaV;

    @Positive
    @NotNull(message = "El total no puede estar vacio")private Double total;
}
