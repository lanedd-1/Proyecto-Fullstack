package com.semestral.inventario.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Inventario;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de inventario en memoria")
class InventarioStockRepositoryTest {

    @Autowired
    private InventarioStockRepository inventarioStockRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pasillo pasillo;
    private Estante estante;
    private Ubicacion ubicacion;
    private Inventario inventarioA;
    private Inventario inventarioB;

    @BeforeEach
    void setUp() {
        pasillo = entityManager.persistAndFlush(new Pasillo(null, "Pasillo Principal"));
        estante = entityManager.persistAndFlush(new Estante(null, "Estante A"));
        ubicacion = entityManager.persistAndFlush(new Ubicacion(null, pasillo, estante));

        inventarioA = entityManager.persistAndFlush(new Inventario(null, 10, 1L, ubicacion));
        inventarioB = entityManager.persistAndFlush(new Inventario(null, 5, 2L, ubicacion));
    }

    @Test
    @DisplayName("Buscar inventario por id de ubicación")
    void whenFindByUbicacionId_thenReturnInventarioList() {
        List<Inventario> inventarios = inventarioStockRepository.findByUbicacionId(ubicacion.getIdPasEst());

        assertNotNull(inventarios);
        assertEquals(2, inventarios.size());
    }

    @Test
    @DisplayName("Buscar inventario por id de producto")
    void whenFindByIdProd_thenReturnItemList() {
        List<Inventario> inventarios = inventarioStockRepository.findByIdProd(1L);

        assertNotNull(inventarios);
        assertEquals(1, inventarios.size());
        assertEquals(10, inventarios.get(0).getStock());
    }

    @Test
    @DisplayName("Buscar inventario por producto y ubicación")
    void whenFindByProductoYUbicacion_thenReturnOptionalInventario() {
        var optional = inventarioStockRepository.findByProductoYUbicacion(2L, ubicacion.getIdPasEst());

        assertTrue(optional.isPresent());
        assertEquals(5, optional.get().getStock());
    }
}

