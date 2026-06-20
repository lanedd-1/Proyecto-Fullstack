package com.semestral.gestion_direccion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.semestral.gestion_direccion.model.Direccion;
@Repository
public interface DireccionRepository extends JpaRepository<Direccion,Long> {
    @Query("select d from Direccion d join fetch d.comuna c join fetch c.region")
    List<Direccion> findAllWithComunaAndRegion();

    @Query("select d from Direccion d join fetch d.comuna c join fetch c.region where d.idDireccion = :id")
    Optional<Direccion> findByIdWithComunaAndRegion(@Param("id") Long id);
}
