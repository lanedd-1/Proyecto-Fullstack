package com.semestral.venta.dto;



import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponseDTO {


    private LocalDateTime idVenta;

    private String fechaV;

    private Double total;
}
