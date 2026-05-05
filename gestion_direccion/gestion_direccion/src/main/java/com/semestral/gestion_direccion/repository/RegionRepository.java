package com.semestral.gestion_direccion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.gestion_direccion.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region,Long>{

}
