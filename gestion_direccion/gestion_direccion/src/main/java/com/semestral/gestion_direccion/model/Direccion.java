package com.semestral.gestion_direccion.model;

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

@Table(name = "direccion")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id_direccion;
    @Column(name = "calle",nullable = false)
    private String calle;
    @Column(name = "numero",nullable = false)
    private String numero;
    @ManyToOne
    @JoinColumn(name = "id_comuna",nullable = false)
    private Comuna comuna;

    //falta conectar con estado
}
