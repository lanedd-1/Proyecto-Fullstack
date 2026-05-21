package com.semestral.inventario.model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasillo;

    @Column(nullable = false, length = 200)
    private String nombrePasillo;


}
