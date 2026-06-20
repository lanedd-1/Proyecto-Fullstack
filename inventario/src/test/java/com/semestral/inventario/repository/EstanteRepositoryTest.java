package com.semestral.inventario.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.inventario.model.Estante;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de estantes en memoria")
class EstanteRepositoryTest {

    @Autowired
    private EstanteRepository estanteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Estante estanteA;
    private Estante estanteB;

    @BeforeEach
    void setUp() {
        estanteA = entityManager.persistAndFlush(new Estante(null, "Estante A"));
        estanteB = entityManager.persistAndFlush(new Estante(null, "Estante B"));
    }

    @Test
    @DisplayName("Consultar estantes guardados")
    void whenFindAll_thenReturnAllShelves() {
        var estantes = estanteRepository.findAll();

        assertNotNull(estantes);
        assertEquals(2, estantes.size());
    }

    @Test
    @DisplayName("Buscar estante por id")
    void whenFindById_thenReturnEstante() {
        var optional = estanteRepository.findById(estanteA.getIdEstante());

        assertTrue(optional.isPresent());
        assertEquals("Estante A", optional.get().getNombreEstante());
    }
}

