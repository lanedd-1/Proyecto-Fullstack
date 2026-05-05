package com.semestral.gestion_direccion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.gestion_direccion.model.Direccion;
@Repository
public interface DireccionRepository extends JpaRepository<Direccion,Long> {

}
