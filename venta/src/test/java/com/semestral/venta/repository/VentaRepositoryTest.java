package com.semestral.venta.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.venta.model.Venta;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Pruebas del repositorio Venta en memoria")
public class VentaRepositoryTest {

	@Autowired
	private VentaRepository ventaRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Venta ventaPersistida;

	@BeforeEach
	void setUp() {
		ventaPersistida = new Venta(null, LocalDateTime.now(), 0.0, new ArrayList<>());
		ventaPersistida = entityManager.persistAndFlush(ventaPersistida);
	}

	@Test
	void whenFindByIdVenta_thenReturnVenta() {
		List<Venta> found = ventaRepository.findByIdVenta(ventaPersistida.getIdVenta());
		assertNotNull(found);
		assertEquals(1, found.size());
		assertEquals(ventaPersistida.getIdVenta(), found.get(0).getIdVenta());
	}

	@Test
	void findByIdVenta_shouldReturnEmptyList_whenNotExists() {
		List<Venta> found = ventaRepository.findByIdVenta(999L);
		assertNotNull(found);
		assertEquals(0, found.size());
	}

	@Test
	void findAll_shouldReturnAllVentas() {
		Venta venta2 = new Venta(null, LocalDateTime.now(), 50.0, new ArrayList<>());
		entityManager.persistAndFlush(venta2);

		List<Venta> result = ventaRepository.findAll();
		assertTrue(result.size() >= 2);
	}

	@Test
	void findById_shouldReturnVenta_whenExists() {
		Optional<Venta> result = ventaRepository.findById(ventaPersistida.getIdVenta());
		assertTrue(result.isPresent());
		assertEquals(ventaPersistida.getIdVenta(), result.get().getIdVenta());
	}

	@Test
	void findById_shouldReturnEmpty_whenNotExists() {
		Optional<Venta> result = ventaRepository.findById(999L);
		assertFalse(result.isPresent());
	}

	@Test
	void save_shouldPersistVenta() {
		Venta newVenta = new Venta(null, LocalDateTime.now(), 100.0, new ArrayList<>());
		Venta saved = ventaRepository.save(newVenta);

		assertNotNull(saved.getIdVenta());
		Optional<Venta> retrieved = ventaRepository.findById(saved.getIdVenta());
		assertTrue(retrieved.isPresent());
		assertEquals(100.0, retrieved.get().getTotal());
	}

	@Test
	void update_shouldModifyVenta() {
		ventaPersistida.setTotal(150.0);
		Venta updated = ventaRepository.save(ventaPersistida);
		entityManager.flush();

		Optional<Venta> result = ventaRepository.findById(updated.getIdVenta());
		assertTrue(result.isPresent());
		assertEquals(150.0, result.get().getTotal());
	}

	@Test
	void delete_shouldRemoveVenta() {
		Long ventaIdToDelete = ventaPersistida.getIdVenta();
		ventaRepository.deleteById(ventaIdToDelete);
		entityManager.flush();

		Optional<Venta> result = ventaRepository.findById(ventaIdToDelete);
		assertFalse(result.isPresent());
	}

	@Test
	void saveMultipleVentas_shouldPersistAll() {
		Venta venta1 = new Venta(null, LocalDateTime.now(), 10.0, new ArrayList<>());
		Venta venta2 = new Venta(null, LocalDateTime.now(), 20.0, new ArrayList<>());
		Venta venta3 = new Venta(null, LocalDateTime.now(), 30.0, new ArrayList<>());

		ventaRepository.saveAll(List.of(venta1, venta2, venta3));
		entityManager.flush();

		List<Venta> result = ventaRepository.findAll();
		assertTrue(result.size() >= 3);
	}

	@Test
	void findByIdVenta_shouldReturnMultipleVentas_withSameId() {
		Venta venta2 = new Venta(null, LocalDateTime.now(), 50.0, new ArrayList<>());
		venta2 = entityManager.persistAndFlush(venta2);

		List<Venta> found = ventaRepository.findByIdVenta(venta2.getIdVenta());
		assertEquals(1, found.size());
	}

	@Test
	void findByIdVenta_shouldHandleNullId() {
		List<Venta> result = ventaRepository.findByIdVenta(null);
		assertNotNull(result);
	}
}
