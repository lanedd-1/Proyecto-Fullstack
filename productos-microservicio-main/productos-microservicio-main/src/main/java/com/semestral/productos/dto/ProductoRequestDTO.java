package com.semestral.productos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "El SKU no puede estar vacio")
    private String sku;
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombreProd;
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String descProd;
    @NotNull(message = "El precio no puede estar vacio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precioUnitario;
    @NotBlank(message = "La URL no puede estar vacia")
    private String foto;
    @NotNull(message = "El stock no puede estar vacio")
    @Positive(message = "El stock debe ser mayor a 0")
    private Long stock;
    @NotNull(message = "La categoría no puede estar vacía")
    private Long idCat;

}
