package com.semestral.venta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.venta.model.Detalle;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long>{


    List<Detalle> findByIdDetalle(Long idDetalle);

}
