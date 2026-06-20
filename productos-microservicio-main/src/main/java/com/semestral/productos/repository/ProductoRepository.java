package com.semestral.productos.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.semestral.productos.model.Productos;

public interface ProductoRepository extends JpaRepository<Productos, Long> {


    List<Productos> findByNombreProdIgnoreCase(String nombre);


    List<Productos> findByPrecioUnitarioLessThanEqual(BigDecimal precio);

    @Query("SELECT p FROM Productos p WHERE p.sku = :sku")
    List<Productos> encontrarProductosPorSku(@Param("sku") String sku);



}
