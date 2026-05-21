package com.semestral.inventario.model;


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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasEst;


    @ManyToOne
    @JoinColumn(name = "PasilloId", nullable = false)
    private Pasillo idPasillo;

    @ManyToOne
    @JoinColumn(name = "EstanteId", nullable = false)
    private Estante idEstante;


}
