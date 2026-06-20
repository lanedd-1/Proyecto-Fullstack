package com.joyeria.gestion_configuracion.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.joyeria.gestion_configuracion.model.Configuracion;
import com.joyeria.gestion_configuracion.repository.ConfiguracionRepository;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de configuracion en memoria")
public class ConfiguracionRepositoryTest {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Configuracion configEjemplo;

    @BeforeEach
    void setUp() {
        configEjemplo = entityManager.persistAndFlush(
                new Configuracion(1L, 8, 20, true, true, true, true, "!@#$%&*"));
    }


    @Test
    @DisplayName("findById() debe retornar Optional con la configuracion cuando existe")
    void findById_debeRetornarConfiguracion_cuandoExiste() {
        Optional<Configuracion> resultado = configuracionRepository.findById(configEjemplo.getIdConfiguracion());

        assertTrue(resultado.isPresent());
        assertEquals(8, resultado.get().getLongitudMinima());
        assertEquals(20, resultado.get().getLongitudMaxima());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Configuracion> resultado = configuracionRepository.findById(99999L);

        assertFalse(resultado.isPresent());
    }


    @Test
    @DisplayName("save() debe actualizar los campos de la configuracion existente")
    void save_debeActualizarConfiguracionExistente() {
        configEjemplo.setLongitudMinima(10);
        configEjemplo.setLongitudMaxima(30);

        Configuracion actualizada = configuracionRepository.save(configEjemplo);

        assertNotNull(actualizada);
        assertEquals(10, actualizada.getLongitudMinima());
        assertEquals(30, actualizada.getLongitudMaxima());
    }
}
