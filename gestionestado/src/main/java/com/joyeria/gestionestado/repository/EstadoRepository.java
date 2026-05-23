package com.joyeria.gestionestado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joyeria.gestionestado.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado,Long> {

    Optional<Estado> findByNombreEstadoIgnoreCase(String nombreEstado);

    
    boolean existsByNombreEstadoIgnoreCase(String nombreEstado);

}
