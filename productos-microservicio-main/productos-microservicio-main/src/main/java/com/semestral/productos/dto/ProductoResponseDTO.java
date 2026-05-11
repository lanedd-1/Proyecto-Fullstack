package com.semestral.productos.dto;

import java.math.BigDecimal;

import com.semestral.productos.model.Categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {

    private Long id;
    private String sku;
    private String nombreProd;
    private String descProd;
    private BigDecimal precioUnitario;
    private String foto;
    private Long stock;
    private Categoria idCat;
}
