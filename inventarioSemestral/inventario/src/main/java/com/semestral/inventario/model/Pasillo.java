package com.semestral.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pasillo")
public class Pasillo {
    
    @Schema(
        description = "ID del pasillo",
        example = "1"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasillo;
    
    @Schema(
        description = "Nombre del pasillo",
        example = "Pasillo A"
    )
    @Column(nullable = false, length = 200)
    private String nombrePasillo;


}
