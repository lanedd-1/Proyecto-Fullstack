package com.semestral.venta.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Pruebas del repositorio Detalle en memoria")
public class DetalleRepositoryTest {

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Venta ventaPersistida;

    @BeforeEach
    void setUp() {
        ventaPersistida = new Venta(null, LocalDateTime.now(), 0.0, null);
        ventaPersistida = entityManager.persistAndFlush(ventaPersistida);
    }

    @Test
    void saveAndFindOperations_shouldWork() {
        Detalle d = new Detalle(null, 2, 20.0, ventaPersistida, 5L);

        Detalle saved = entityManager.persistAndFlush(d);

        List<Detalle> byIdDetalle = detalleRepository.findByIdDetalle(saved.getIdDetalle());
        assertEquals(1, byIdDetalle.size());

        Optional<Detalle> byVentaAndProducto = detalleRepository.findByIdVenta_IdVentaAndProductoId(ventaPersistida.getIdVenta(), 5L);
        assertTrue(byVentaAndProducto.isPresent());
    }
}

