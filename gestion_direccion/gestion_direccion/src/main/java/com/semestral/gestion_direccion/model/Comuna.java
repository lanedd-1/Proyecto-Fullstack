package com.semestral.gestion_direccion.model;

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

@Table(name = "comuna")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa a una comuna dentro del sistema de gestión")
public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único autogenerado por la base de datos", 
        example = "1", 
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idComuna;
    @Schema(description = "Nombre de la comuna registrada", example = "Metropolitana")
    @Column(name = "nombreComuna")
    private String nombreC;
    @ManyToOne
    @JoinColumn(name = "idRegion")
    @Schema(description = "Objeto completo de la region asignado con sus respectivos detalles")
    private Region region;
    
}
