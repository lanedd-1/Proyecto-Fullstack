package com.semestral.productos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
    
    @Schema(
        description = "SKU del producto",
        example = "SKU-001"
    )
    @NotBlank(message = "El SKU no puede estar vacio")
    private String sku;

    @Schema(
        description = "Nombre del producto",
        example = "Collar de oro"
    )
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombreProd;

    @Schema(
        description = "Descripción del producto",
        example = "Collar de oro de 18K"
    )
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String descProd;
    
    @Schema(
        description = "Precio unitario del producto",
        example = "10999.99"
    )
    @NotNull(message = "El precio no puede estar vacio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precioUnitario;

    @Schema(
        description = "URL de la imagen del producto",
        example = "https://example.com/foto.jpg"
    )
    @NotBlank(message = "La URL no puede estar vacia")
    private String foto;

    @Schema(
        description = "ID de la categoría del producto"
    )
    @NotNull(message = "La categoría no puede estar vacía")
    private Long idCat;

}
