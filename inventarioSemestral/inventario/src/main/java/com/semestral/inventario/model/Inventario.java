package com.semestral.inventario.model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInv;
    
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, length = 50)
    private Long idProd;

    @ManyToOne
    @JoinColumn(name = "Ubicacion")
    private Ubicacion idPasEstante;

    

}
