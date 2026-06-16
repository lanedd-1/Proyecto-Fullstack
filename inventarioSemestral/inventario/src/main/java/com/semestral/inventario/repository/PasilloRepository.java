package com.semestral.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.semestral.inventario.model.Pasillo;


public interface PasilloRepository extends JpaRepository<Pasillo, Long> {
   
}
