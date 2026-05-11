package com.semestral.productos.model;

import java.math.BigDecimal;

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
@Table(name = "productos")
public class Productos {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(name = "sku", nullable = false, unique = true, length = 20)
private String sku;


@Column(nullable = false, length = 100)
private String nombreProd;


@Column(name = "descripcion", length = 300)
private String descProd;



@Column(name = "precio_unit")
private BigDecimal precioUnitario;
    

@Column(name = "foto", length = 500)
private String foto;


@Column(name = "stock_total", nullable = false)
private Long stock;


@ManyToOne
@JoinColumn(name = "categoria_id", nullable = true)
private Categoria idCat;



}
