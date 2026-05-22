package com.semestral.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.semestral.inventario.model.Inventario;

@Repository
public interface InventarioStockRepository extends JpaRepository<Inventario, Long> {

 List<Inventario> findByUbicacionId(Long idUbicacion);

 List<Inventario> findByIdProducto(Object idProd);

 Optional<Inventario> findByIdProductoAndUbicacionId(Object idProd, Long idUbicacion);

}
