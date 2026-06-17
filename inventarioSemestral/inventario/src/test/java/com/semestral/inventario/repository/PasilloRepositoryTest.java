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

import com.semestral.inventario.model.Pasillo;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de pasillos en memoria")
class PasilloRepositoryTest {

    @Autowired
    private PasilloRepository pasilloRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pasillo pasilloA;
    private Pasillo pasilloB;

    @BeforeEach
    void setUp() {
        pasilloA = entityManager.persistAndFlush(new Pasillo(null, "Pasillo A"));
        pasilloB = entityManager.persistAndFlush(new Pasillo(null, "Pasillo B"));
    }

    @Test
    @DisplayName("Consultar pasillos guardados")
    void whenFindAll_thenReturnAllAisles() {
        var pasillos = pasilloRepository.findAll();

        assertNotNull(pasillos);
        assertEquals(2, pasillos.size());
    }

    @Test
    @DisplayName("Buscar pasillo por id")
    void whenFindById_thenReturnPasillo() {
        var optional = pasilloRepository.findById(pasilloB.getIdPasillo());

        assertTrue(optional.isPresent());
        assertEquals("Pasillo B", optional.get().getNombrePasillo());
    }
}

