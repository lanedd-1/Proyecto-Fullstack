package com.semestral.venta.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detalleVenta")
public class Detalle {
    
    @Schema(
        description = "ID del detalle de venta",
        example = "1"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;
    
    @Schema(
        description = "Cantidad del producto en el detalle de venta",
        example = "2"
    )
    @Positive
    @Column(nullable = false)
    private Integer cantidad;
    
    @Schema(
        description = "Precio del producto en el detalle de venta",
        example = "19999.99"
    )
    @Positive
    @Column(nullable = false)
    private Double subTotal;

    @Schema(
        description = "Venta a la que pertenece el detalle",
        example = "1"
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Venta idVenta;


    @Schema(
        description = "ID del producto en el detalle de venta",
        example = "1"
    )
    @Column(nullable = false)
    private Long productoId;

}

