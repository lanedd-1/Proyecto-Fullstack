package com.joyeria.gestion_envio.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.joyeria.gestion_envio.model.Historial;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de Historial en memoria H2")
public class HistorialRepositoryTest {

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Historial historialGuardado;

    @BeforeEach
    void setUp() {
        Historial historial = new Historial();
        historial.setIdEnvio(1L);
        historial.setFecha(LocalDateTime.now());
        historial.setEstado("ENTREGADO");
        historial.setIdUsuario(5L);

        historialGuardado = entityManager.persistAndFlush(historial);
    }

    @Test
    @DisplayName("findAll() debe retornar todo el historial guardado")
    void findAll_debeRetornarTodoElHistorial() {
        List<Historial> historiales = historialRepository.findAll();

        assertNotNull(historiales);
        assertEquals(1, historiales.size());
    }

    @Test
    @DisplayName("findById() debe retornar el registro de historial cuando existe")
    void findById_debeRetornarHistorial_cuandoExiste() {
        Optional<Historial> resultado = historialRepository.findById(historialGuardado.getIdHistorial());

        assertTrue(resultado.isPresent());
        assertEquals("ENTREGADO", resultado.get().getEstado());
        assertEquals(1L, resultado.get().getIdEnvio());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Historial> resultado = historialRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }
}