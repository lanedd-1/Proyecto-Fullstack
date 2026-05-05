package com.semestral.gestion_direccion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_comuna;
    @Column(name = "nombre_comuna")
    private String nombreC;
    @ManyToOne
    @Column(name = "id_region",nullable = false)
    private Region region;
    
}
