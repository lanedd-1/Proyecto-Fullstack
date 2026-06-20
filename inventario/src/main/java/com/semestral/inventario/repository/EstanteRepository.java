package com.semestral.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.semestral.inventario.model.Estante;


public interface EstanteRepository extends JpaRepository<Estante, Long> {
    
}