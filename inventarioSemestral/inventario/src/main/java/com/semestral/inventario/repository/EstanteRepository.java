package com.semestral.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.inventario.model.Estante;

@Repository
public interface EstanteRepository extends JpaRepository<Estante, Long> {
    
}