package com.semestral.gestion_usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.semestral.gestion_usuarios.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol,Long>{  
    Optional findByNombreRol(String nombreRol);

    Optional<Rol> findByNombreRolIgnoreCase(String nombreRol);

}
