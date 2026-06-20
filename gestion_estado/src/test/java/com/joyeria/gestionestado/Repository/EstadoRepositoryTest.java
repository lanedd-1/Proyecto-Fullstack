package com.joyeria.gestionestado.repository;

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

import com.joyeria.gestionestado.model.Estado;
import com.joyeria.gestionestado.repository.EstadoRepository;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de estados en memoria H2")
public class EstadoRepositoryTest {


    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private TestEntityManager entityManager;

    // Variables para datos insertados en memoria antes de cada test
    private Estado activo;
    private Estado inactivo;

    // Insertar datos antes de cada test
    @BeforeEach
    void setUp() {
        activo = entityManager.persistAndFlush(
                new Estado(null, "Activo"));
        inactivo = entityManager.persistAndFlush(
                new Estado(null, "Inactivo"));
    }

    @Test
    @DisplayName("findAll() debe retornar todos los estados insertados")
    void findAll_debeRetornarTodosLosEstados() {
        List<Estado> estados = estadoRepository.findAll();

        assertNotNull(estados);
        assertEquals(2, estados.size());
    }

    @Test
    @DisplayName("findById() debe retornar Optional con el estado cuando existe")
    void findById_debeRetornarEstado_cuandoExiste() {
        Optional<Estado> resultado = estadoRepository.findById(activo.getIdEstado());

        assertTrue(resultado.isPresent());
        assertEquals("Activo", resultado.get().getNombreEstado());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Estado> resultado = estadoRepository.findById(99999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("existsByNombreEstadoIgnoreCase() retorna true cuando el nombre existe")
    void existsByNombre_debeRetornarTrue_cuandoExiste() {
        boolean existe = estadoRepository.existsByNombreEstadoIgnoreCase("activo");

        assertTrue(existe);
    }

    @Test
    @DisplayName("existsByNombreEstadoIgnoreCase() retorna false cuando el nombre no existe")
    void existsByNombre_debeRetornarFalse_cuandoNoExiste() {
        boolean existe = estadoRepository.existsByNombreEstadoIgnoreCase("Cancelado");

        assertFalse(existe);
    }

}


