package com.semestral.gestion_direccion.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.gestion_direccion.model.Comuna;
import com.semestral.gestion_direccion.model.Region;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de Comunas en memoria H2")
public class ComunaRepositoryTest {

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Comuna comunaQuilicura;

    @BeforeEach
    void setUp() {
        Region r = new Region();
        r.setNombreRegion("Metropolitana");
        Region regionMetropolitana = entityManager.persistAndFlush(r);
        
        Comuna c = new Comuna();
        c.setNombreC("Quilicura");
        c.setRegion(regionMetropolitana);
        comunaQuilicura = entityManager.persistAndFlush(c);
    }

    @Test
    @DisplayName("findAll() debe retornar todas las comunas guardadas")
    void findAll_debeRetornarTodasLasComunas() {
        List<Comuna> comunas = comunaRepository.findAll();

        assertNotNull(comunas);
        assertEquals(1, comunas.size());
    }

    @Test
    @DisplayName("findById() debe retornar la comuna cuando existe")
    void findById_debeRetornarComuna_cuandoExiste() {
        Optional<Comuna> resultado = comunaRepository.findById(comunaQuilicura.getIdComuna());

        assertTrue(resultado.isPresent());
        assertEquals("Quilicura", resultado.get().getNombreC());
        assertEquals("Metropolitana", resultado.get().getRegion().getNombreRegion());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Comuna> resultado = comunaRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }
}