package com.semestral.inventario.model;

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
@Table(name = "inventarioStock")
public class Inventario {
    
    @Schema(
        description = "ID del inventario",
        example = "1"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInv;
    
    @Schema(
        description = "Cantidad del producto en el inventario",
        example = "10"
    )
    @Column(nullable = false)
    private Integer stock;

    @Schema(
        description = "ID del producto",
        example = "1"
    )
    @Column(nullable = false, length = 50)
    private Long idProd;

    @Schema(
        description = "ID del pasillo",
        example = "1"
    )
    @ManyToOne
    @JoinColumn(name = "Ubicacion")
    private Ubicacion idPasEstante;

    

}
