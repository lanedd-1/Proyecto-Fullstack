package com.semestral.inventario.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de ubicaciones en memoria")
class UbicacionRepositoryTest {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pasillo pasillo;
    private Estante estante;
    private Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        pasillo = entityManager.persistAndFlush(new Pasillo(null, "Pasillo Central"));
        estante = entityManager.persistAndFlush(new Estante(null, "Estante 1"));

        ubicacion = entityManager.persistAndFlush(new Ubicacion(null, pasillo, estante));
    }

    @Test
    @DisplayName("Buscar ubicación por pasillo y estante")
    void whenFindByPasilloAndEstante_thenReturnUbicacion() {
        var optional = ubicacionRepository.findByPasilloAndEstante(pasillo.getIdPasillo(), estante.getIdEstante());

        assertTrue(optional.isPresent());
        assertEquals(ubicacion.getIdPasEst(), optional.get().getIdPasEst());
    }
}

