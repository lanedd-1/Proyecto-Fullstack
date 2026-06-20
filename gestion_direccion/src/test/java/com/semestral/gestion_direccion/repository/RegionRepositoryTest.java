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

import com.semestral.gestion_direccion.model.Region;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de Regiones en memoria H2")
public class RegionRepositoryTest {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Region regionMetropolitana;

    @BeforeEach
    void setUp() {
        Region r = new Region();
        r.setNombreRegion("Metropolitana");
        regionMetropolitana = entityManager.persistAndFlush(r);
    }

    @Test
    @DisplayName("findAll() debe retornar todas las regiones guardadas")
    void findAll_debeRetornarTodasLasRegiones() {
        List<Region> regiones = regionRepository.findAll();

        assertNotNull(regiones);
        assertEquals(1, regiones.size());
    }

    @Test
    @DisplayName("findById() debe retornar la región cuando existe")
    void findById_debeRetornarRegion_cuandoExiste() {
        Optional<Region> resultado = regionRepository.findById(regionMetropolitana.getIdRegion());

        assertTrue(resultado.isPresent());
        assertEquals("Metropolitana", resultado.get().getNombreRegion());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Region> resultado = regionRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }
}