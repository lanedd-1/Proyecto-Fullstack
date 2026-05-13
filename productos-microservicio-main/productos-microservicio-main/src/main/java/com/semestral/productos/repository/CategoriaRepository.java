package com.semestral.productos.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.semestral.productos.model.Categoria;
import java.util.List;


public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
    List<Categoria> findByNombreCatIgnoreCase(String nombreCat);

}
