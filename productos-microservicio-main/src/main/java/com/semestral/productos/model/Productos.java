package com.semestral.productos.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;





@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Productos {

@Schema(
    description = "ID del producto",
    example = "1"
)
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long idProd;

@Schema(
    description = "SKU del producto",
    example = "SKU-001"
)
@Column(name = "sku", nullable = false, unique = true, length = 20)
private String sku;

@Schema(
    description = "Nombre del producto",
    example = "Collar de perlas"
)
@Column(nullable = false, length = 100)
private String nombreProd;

@Schema(
    description = "Descripción del producto",
    example = "Un elegante collar de perlas para ocasiones especiales."
)

@Column(name = "descripcion", length = 300)
private String descProd;


@Schema(
    description = "Precio unitario del producto",
    example = "40999.99"
)
@Column(name = "precio_unit")
private BigDecimal precioUnitario;
    
@Schema(
    description = "URL de la imagen del producto",
    example = "https://example.com/foto.jpg"
)
@Column(name = "foto", length = 500)
private String foto;



@Schema(
    description = "ID de la categoría del producto (Tomar en cuenta que la categoria debe existir previamente)",
    example = "1"
)
@ManyToOne
@JoinColumn(name = "categoria_id", nullable = true)
private Categoria idCat;



}
