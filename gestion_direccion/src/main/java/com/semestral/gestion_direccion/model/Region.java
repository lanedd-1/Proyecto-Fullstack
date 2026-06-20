package com.semestral.gestion_direccion.model;

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

@Table(name = "region")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único autogenerado por la base de datos", 
        example = "1", 
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idRegion;

    @Column(name = "nombreRegion",nullable = false)
    @Schema(description = "Nombre de la region registrada", example = "Metropolitana")
    private String nombreRegion;
}
