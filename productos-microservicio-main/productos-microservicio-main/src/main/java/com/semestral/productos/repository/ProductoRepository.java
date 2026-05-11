package com.semestral.productos.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.semestral.productos.model.Productos;

public interface ProductoRepository extends JpaRepository<Productos, Long> {

    // Sin guiones bajos
    List<Productos> findByNombreProdIgnoreCase(String nombre);

    // Sin guiones bajos
    List<Productos> findByPrecioUnitarioLessThanEqual(BigDecimal precio);

    // Consultas personalizadas (Estas están bien, pero asegúrate 
    // de que p.SKU y p.nombre_prod coincidan con los nombres de 
    // las variables en tu clase @Entity)
    @Query("SELECT p FROM Productos p WHERE p.sku = :sku")
    List<Productos> encontrarProductosPorSku(@Param("sku") String sku);



}
