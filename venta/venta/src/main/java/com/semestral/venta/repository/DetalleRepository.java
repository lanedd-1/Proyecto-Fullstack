package com.semestral.venta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.semestral.venta.model.Detalle;

public interface DetalleRepository extends JpaRepository<Detalle, Long>{

    List<Detalle> findByIdDetalle(Long idDetalle);
    
    Optional<Detalle> findByIdVenta_IdVentaAndProductoId(Long ventaId, Long productoId);

}
