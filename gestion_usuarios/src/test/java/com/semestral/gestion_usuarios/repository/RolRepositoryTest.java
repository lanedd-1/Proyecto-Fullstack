package com.semestral.gestion_usuarios.repository;

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
import org.springframework.test.context.TestPropertySource;

import com.semestral.gestion_usuarios.model.Rol;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de Roles en memoria H2")
public class RolRepositoryTest {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        Rol r = new Rol();
        r.setNombreRol("ADMIN");
        rolAdmin = entityManager.persistAndFlush(r);
    }

    @Test
    @DisplayName("findAll() debe retornar todos los roles guardados")
    void findAll_debeRetornarTodosLosRoles() {
        List<Rol> roles = rolRepository.findAll();

        assertNotNull(roles);
        assertEquals(1, roles.size());
    }

    @Test
    @DisplayName("findById() debe retornar el rol cuando existe")
    void findById_debeRetornarRol_cuandoExiste() {
        Optional<Rol> resultado = rolRepository.findById(rolAdmin.getIdRol());

        assertTrue(resultado.isPresent());
        assertEquals("ADMIN", resultado.get().getNombreRol());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Rol> resultado = rolRepository.findById(99L); 

        assertFalse(resultado.isPresent());
    }
}