package com.semestral.productos.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.semestral.productos.model.Productos;

public interface ProductoRepository extends JpaRepository<Productos, Long>{

    //creacion de metodos para la utilizacion del SQL e interpretacion
    List<Productos> findByNombre_prodIgnoreCase(String nombre);

    List<Productos> findByPrecio_unitarioLessThanEqual(BigDecimal precio);


    //consultas personalizadas
    @Query("SELECT p FROM Productos p WHERE p.SKU = :sku")
    List<Productos> encontrarProductosPorSku(@Param("sku") String sku);

    @Query("SELECT p FROM Productos p WHERE p.nombre_prod LIKE %:nombre_prod%")
    List<Productos> encontrarProductosPorNombre(@Param("nombre_prod") String nombre_prod);


    
}
