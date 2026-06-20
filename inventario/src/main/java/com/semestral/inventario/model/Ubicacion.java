package com.semestral.inventario.model;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "ubicacion")
public class Ubicacion {

    @Schema(
        description = "ID de la ubicación",
        example = "1"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasEst;

    @Schema(
        description = "ID del pasillo",
        example = "1"
    )
    @ManyToOne
    @JoinColumn(name = "PasilloId", nullable = false)
    private Pasillo idPasillo;

    @Schema(
        description = "ID del estante",
        example = "1"
    )
    @ManyToOne
    @JoinColumn(name = "EstanteId", nullable = false)
    private Estante idEstante;


}
