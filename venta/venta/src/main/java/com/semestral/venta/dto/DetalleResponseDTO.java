package com.semestral.venta.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleResponseDTO {

    private Long idDetalle;

    private Integer cantidad;

    private Double subTotal;

}
