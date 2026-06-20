package com.semestral.venta.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "venta")
public class Venta {

    @Schema(
        description = "ID de la venta",
        example = "1"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @Schema(
        description = "Fecha de la venta",
        example = "2024-06-15T14:30:00"
    )
    @Column(nullable = false)
    private LocalDateTime fechaV;

    @Schema(
        description = "Total de la venta",
        example = "39999.98"
    )
    @PositiveOrZero
    @Column(name = "totalVenta", nullable = false)
    private Double total = 0.0;

    @Schema(
        description = "Lista de detalles asociados a la venta",
        example = "idDetalle: 1, cantidad: 2, subTotal: 19999.99, productoId: 1"
    )
    @OneToMany(mappedBy = "idVenta", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Detalle> detalles = new ArrayList<>();
}
