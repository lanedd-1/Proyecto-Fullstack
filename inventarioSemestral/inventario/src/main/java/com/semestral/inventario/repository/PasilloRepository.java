package com.semestral.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.inventario.model.Pasillo;

@Repository
public interface PasilloRepository extends JpaRepository<Pasillo, Long> {
   
}
