package com.semestral.venta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.venta.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>{

List<Venta> findByIdVenta(Long idVenta);



}
