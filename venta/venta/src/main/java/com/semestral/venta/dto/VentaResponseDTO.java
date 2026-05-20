package com.semestral.venta.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponseDTO {


    private Long idVenta;

    private String fechaV;

    private Double total;
}
