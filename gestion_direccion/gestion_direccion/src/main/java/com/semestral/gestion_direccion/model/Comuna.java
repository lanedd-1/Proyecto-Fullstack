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

@Table(name = "comuna")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComuna;
    @Column(name = "nombreComuna")
    private String nombreC;
    @ManyToOne
    @JoinColumn(name = "idRegion")
    private Region region;
    
}
