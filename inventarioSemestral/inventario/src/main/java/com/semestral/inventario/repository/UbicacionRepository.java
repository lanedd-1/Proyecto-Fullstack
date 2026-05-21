package com.semestral.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.semestral.inventario.model.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long>{

    Optional<Ubicacion> findByPasilloIdAndEstanteId(Long idPasillo, Long idEstante);
}
