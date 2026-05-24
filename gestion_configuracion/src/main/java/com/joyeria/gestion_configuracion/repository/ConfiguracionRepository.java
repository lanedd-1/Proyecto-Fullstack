package com.joyeria.gestion_configuracion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joyeria.gestion_configuracion.model.Configuracion;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion,Long>{

}
