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

import com.joyeria.gestion_envio.model.Envio;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de Envíos en memoria H2")
public class EnvioRepositoryTest {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Envio envioGuardado;

    @BeforeEach
    void setUp() {
        Envio envio = new Envio();
        envio.setFechaEnvio(LocalDateTime.now());
        envio.setFechaRecep(LocalDateTime.now().plusDays(3));
        envio.setIdVenta(50L);
        envio.setIdDireccion(10L);
        envio.setEstado("EN_TRANSITO");

        envioGuardado = entityManager.persistAndFlush(envio);
    }

    @Test
    @DisplayName("findAll() debe retornar todos los envíos guardados")
    void findAll_debeRetornarTodosLosEnvios() {
        List<Envio> envios = envioRepository.findAll();

        assertNotNull(envios);
        assertEquals(1, envios.size());
    }

    @Test
    @DisplayName("findById() debe retornar el envío cuando existe")
    void findById_debeRetornarEnvio_cuandoExiste() {
        Optional<Envio> resultado = envioRepository.findById(envioGuardado.getIdEnvio());

        assertTrue(resultado.isPresent());
        assertEquals("EN_TRANSITO", resultado.get().getEstado());
        assertEquals(50L, resultado.get().getIdVenta());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Envio> resultado = envioRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }
}