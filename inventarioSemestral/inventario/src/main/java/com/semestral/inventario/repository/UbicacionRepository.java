package com.semestral.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.semestral.inventario.model.Ubicacion;
  
    // Usamos @Query para apuntar exactamente a los campos de tus clases
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    @Query("SELECT u FROM Ubicacion u WHERE u.idPasillo.idPasillo = :idPasillo AND u.idEstante.idEstante = :idEstante")
    Optional<Ubicacion> findByPasilloAndEstante(@Param("idPasillo") Long idPasillo, @Param("idEstante") Long idEstante);
}
