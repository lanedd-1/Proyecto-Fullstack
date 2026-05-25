package com.semestral.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.semestral.inventario.model.Inventario;

@Repository
public interface InventarioStockRepository extends JpaRepository<Inventario, Long> {
    

    @Query("SELECT i FROM Inventario i WHERE i.idPasEstante.idPasEst = :idUbicacion")
    List<Inventario> findByUbicacionId(@Param("idUbicacion") Long idUbicacion);

    List<Inventario> findByIdProd(Long idProd);

    @Query("SELECT i FROM Inventario i WHERE i.idProd = :idProd AND i.idPasEstante.idPasEst = :idPasEst")
    Optional<Inventario> findByProductoYUbicacion(@Param("idProd") Long idProd, @Param("idPasEst") Long idPasEst);
    

}
