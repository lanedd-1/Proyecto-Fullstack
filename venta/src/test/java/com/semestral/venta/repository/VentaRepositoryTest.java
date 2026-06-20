package com.semestral.venta.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
