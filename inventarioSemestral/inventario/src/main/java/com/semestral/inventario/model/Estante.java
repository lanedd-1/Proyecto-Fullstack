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
@Table(name = "estante")
public class Estante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstante;

    @Column(nullable = false, length = 100)
    private String nombreEstante;
}
