package com.semestral.productos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.productos.model.Categoria;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test de integración de CategoriaRepository")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("findByNombreCatIgnoreCase() devuelve categorías sin distinguir mayúsculas")
    void findByNombreCatIgnoreCase_debeRetornarCategoriasIgnorandoMayusculas() {
        Categoria categoria = new Categoria();
        categoria.setNombreCat("Collares");
        Categoria guardada = categoriaRepository.save(categoria);

        List<Categoria> resultado = categoriaRepository.findByNombreCatIgnoreCase("collares");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(guardada.getIdCat(), resultado.get(0).getIdCat());
        assertEquals("Collares", resultado.get(0).getNombreCat());
    }

    @Test
    @DisplayName("findById() encuentra una categoría previamente guardada")
    void findById_debeEncontrarCategoriaGuardada() {
        Categoria categoria = new Categoria();
        categoria.setNombreCat("Anillos");
        Categoria guardada = categoriaRepository.save(categoria);

        Optional<Categoria> resultado = categoriaRepository.findById(guardada.getIdCat());

        assertTrue(resultado.isPresent());
        assertEquals("Anillos", resultado.get().getNombreCat());
    }
}
