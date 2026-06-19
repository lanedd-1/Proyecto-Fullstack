package com.semestral.gestion_direccion.repository;

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

import com.semestral.gestion_direccion.model.Comuna;
import com.semestral.gestion_direccion.model.Direccion;
import com.semestral.gestion_direccion.model.Region;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de Direcciones en memoria H2")
public class DireccionRepositoryTest {

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Direccion direccionCasa;

    @BeforeEach
    void setUp() {
        Region r = new Region();
        r.setNombreRegion("Metropolitana");
        Region regionMetro = entityManager.persistAndFlush(r);

        Comuna c = new Comuna();
        c.setNombreC("Quilicura");
        c.setRegion(regionMetro);
        Comuna comunaQuilicura = entityManager.persistAndFlush(c);

        Direccion d = new Direccion();
        d.setCalle("Avenida Siempre Viva");
        d.setNumero("123");
        d.setComuna(comunaQuilicura);
        d.setIdUsuario(1L);
        d.setIdEstado(1L);
        direccionCasa = entityManager.persistAndFlush(d);
    }

    @Test
    @DisplayName("findAll() debe retornar todas las direcciones guardadas")
    void findAll_debeRetornarTodasLasDirecciones() {
        List<Direccion> direcciones = direccionRepository.findAll();

        assertNotNull(direcciones);
        assertEquals(1, direcciones.size());
    }

    @Test
    @DisplayName("findById() debe retornar la dirección cuando existe")
    void findById_debeRetornarDireccion_cuandoExiste() {
        Optional<Direccion> resultado = direccionRepository.findById(direccionCasa.getIdDireccion());

        assertTrue(resultado.isPresent());
        assertEquals("Avenida Siempre Viva", resultado.get().getCalle());
        assertEquals("123", resultado.get().getNumero());
    }

    @Test
    @DisplayName("findAllWithComunaAndRegion() debe retornar la lista con las relaciones cargadas")
    void findAllWithComunaAndRegion_debeRetornarListaConRelaciones() {
        List<Direccion> resultados = direccionRepository.findAllWithComunaAndRegion();

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Quilicura", resultados.get(0).getComuna().getNombreC());
        assertEquals("Metropolitana", resultados.get(0).getComuna().getRegion().getNombreRegion());
    }

    @Test
    @DisplayName("findByIdWithComunaAndRegion() debe retornar la dirección con sus relaciones cuando existe")
    void findByIdWithComunaAndRegion_debeRetornarConRelaciones() {
        Optional<Direccion> resultado = direccionRepository.findByIdWithComunaAndRegion(direccionCasa.getIdDireccion());

        assertTrue(resultado.isPresent());
        assertEquals("Avenida Siempre Viva", resultado.get().getCalle());
        assertNotNull(resultado.get().getComuna());
        assertEquals("Quilicura", resultado.get().getComuna().getNombreC());
    }

    @Test
    @DisplayName("findByIdWithComunaAndRegion() debe retornar Optional vacio cuando el ID no existe")
    void findByIdWithComunaAndRegion_debeRetornarVacio_cuandoNoExiste() {
        Optional<Direccion> resultado = direccionRepository.findByIdWithComunaAndRegion(999L);

        assertFalse(resultado.isPresent());
    }
}