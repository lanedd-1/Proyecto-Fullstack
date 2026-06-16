package com.semestral.venta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.semestral.venta.model.Venta;


public interface VentaRepository extends JpaRepository<Venta, Long>{

List<Venta> findByIdVenta(Long idVenta);



}
