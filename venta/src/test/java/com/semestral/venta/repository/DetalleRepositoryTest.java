package com.semestral.venta.repository;

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
    private Detalle detallePeristido;

    @BeforeEach
    void setUp() {
        ventaPersistida = new Venta(null, LocalDateTime.now(), 0.0, null);
        ventaPersistida = entityManager.persistAndFlush(ventaPersistida);

        detallePeristido = new Detalle(null, 2, 20.0, ventaPersistida, 5L);
        detallePeristido = entityManager.persistAndFlush(detallePeristido);
    }

    @Test
    void saveAndFindOperations_shouldWork() {
        List<Detalle> byIdDetalle = detalleRepository.findByIdDetalle(detallePeristido.getIdDetalle());
        assertEquals(1, byIdDetalle.size());

        Optional<Detalle> byVentaAndProducto = detalleRepository
            .findByIdVenta_IdVentaAndProductoId(ventaPersistida.getIdVenta(), 5L);
        assertTrue(byVentaAndProducto.isPresent());
    }

    @Test
    void findByIdDetalle_shouldReturnDetalle_whenExists() {
        List<Detalle> result = detalleRepository.findByIdDetalle(detallePeristido.getIdDetalle());
        assertEquals(1, result.size());
        assertEquals(detallePeristido.getIdDetalle(), result.get(0).getIdDetalle());
    }

    @Test
    void findByIdDetalle_shouldReturnEmptyList_whenNotExists() {
        List<Detalle> result = detalleRepository.findByIdDetalle(999L);
        assertEquals(0, result.size());
    }

    @Test
    void findByIdVenta_IdVentaAndProductoId_shouldReturnDetalle_whenExists() {
        Optional<Detalle> result = detalleRepository
            .findByIdVenta_IdVentaAndProductoId(ventaPersistida.getIdVenta(), 5L);
        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getProductoId());
    }

    @Test
    void findByIdVenta_IdVentaAndProductoId_shouldReturnEmpty_whenNotExists() {
        Optional<Detalle> result = detalleRepository
            .findByIdVenta_IdVentaAndProductoId(ventaPersistida.getIdVenta(), 999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_shouldReturnAllDetalles() {
        Detalle detalle2 = new Detalle(null, 3, 30.0, ventaPersistida, 6L);
        entityManager.persistAndFlush(detalle2);

        List<Detalle> result = detalleRepository.findAll();
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_shouldReturnDetalle_whenExists() {
        Optional<Detalle> result = detalleRepository.findById(detallePeristido.getIdDetalle());
        assertTrue(result.isPresent());
        assertEquals(detallePeristido.getIdDetalle(), result.get().getIdDetalle());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Detalle> result = detalleRepository.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void save_shouldPersistDetalle() {
        Detalle newDetalle = new Detalle(null, 5, 50.0, ventaPersistida, 7L);
        Detalle saved = detalleRepository.save(newDetalle);

        assertNotNull(saved.getIdDetalle());
        Optional<Detalle> retrieved = detalleRepository.findById(saved.getIdDetalle());
        assertTrue(retrieved.isPresent());
    }

    @Test
    void delete_shouldRemoveDetalle() {
        Long detalleIdToDelete = detallePeristido.getIdDetalle();
        detalleRepository.deleteById(detalleIdToDelete);
        entityManager.flush();

        Optional<Detalle> result = detalleRepository.findById(detalleIdToDelete);
        assertFalse(result.isPresent());
    }

    @Test
    void findByIdVenta_IdVentaAndProductoId_shouldReturnEmpty_whenVentaIdDoesNotMatch() {
        Optional<Detalle> result = detalleRepository
            .findByIdVenta_IdVentaAndProductoId(999L, 5L);
        assertFalse(result.isPresent());
    }
}

