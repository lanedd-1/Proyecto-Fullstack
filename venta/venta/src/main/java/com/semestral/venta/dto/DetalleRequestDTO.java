package com.semestral.venta.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleRequestDTO {

    @NotNull(message = "La cantidad no puede estar vacia")
     private Integer cantidad;


    @NotNull(message = "El total no puede estar vacio")
     private Double subTotal;

}
