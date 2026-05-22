package com.joyeria.gestion_envio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joyeria.gestion_envio.model.Historial;
@Repository
public interface HistorialRepository extends JpaRepository<Historial,Long> {
    List<Historial> findByEnvioId(Long envioId);

}
