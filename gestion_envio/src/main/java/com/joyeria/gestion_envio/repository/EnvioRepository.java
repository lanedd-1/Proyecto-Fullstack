package com.joyeria.gestion_envio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joyeria.gestion_envio.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByIdVenta(Long idVenta);
}
