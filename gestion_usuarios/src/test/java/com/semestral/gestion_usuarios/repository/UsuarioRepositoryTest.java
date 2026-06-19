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
import com.semestral.gestion_usuarios.model.Usuario;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de usuarios en memoria H2")
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;
    private Usuario usuarioAdmin;
    private Usuario usuarioCliente;

    @BeforeEach
    void setUp() {
        Rol rolA = new Rol();
        rolA.setNombreRol("ADMIN");
        Rol rolAdmin = entityManager.persistAndFlush(rolA);

        Rol rolC = new Rol();
        rolC.setNombreRol("CLIENTE");
        Rol rolCliente = entityManager.persistAndFlush(rolC);

        Usuario u1 = new Usuario();
        u1.setNombreU("Juan Perez");
        u1.setRutU("11111111-1");
        u1.setCorreoU("juan@mail.com");
        u1.setClaveU("12345");
        u1.setRol(rolAdmin);
        u1.setIdEstado(1L);

        Usuario u2 = new Usuario();
        u2.setNombreU("Maria Lopez");
        u2.setRutU("22222222-2");
        u2.setCorreoU("maria@mail.com");
        u2.setClaveU("67890");
        u2.setRol(rolCliente);
        u2.setIdEstado(1L);

        usuarioAdmin = entityManager.persistAndFlush(u1);
        usuarioCliente = entityManager.persistAndFlush(u2);
    }

    @Test
    @DisplayName("findAll() debe retornar todos los usuarios insertados")
    void findAll_debeRetornarTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
    }

    @Test
    @DisplayName("findById() debe retornar Optional con el usuario cuando existe")
    void findById_debeRetornarUsuario_cuandoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findById(usuarioAdmin.getIdUsuario());

        assertTrue(resultado.isPresent());
        assertEquals("Juan Perez", resultado.get().getNombreU());
        assertEquals("juan@mail.com", resultado.get().getCorreoU());
    }

    @Test
    @DisplayName("findById() debe retornar Optional vacio cuando el ID no existe")
    void findById_debeRetornarVacio_cuandoNoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findById(99999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("findByCorreoU() retorna Optional con el usuario cuando el correo existe")
    void findByCorreoU_debeRetornarUsuario_cuandoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findByCorreoU("maria@mail.com");

        assertTrue(resultado.isPresent());
        assertEquals("Maria Lopez", resultado.get().getNombreU());
    }

    @Test
    @DisplayName("findByCorreoU() retorna Optional vacio cuando el correo no existe")
    void findByCorreoU_debeRetornarVacio_cuandoNoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findByCorreoU("inexistente@mail.com");

        assertFalse(resultado.isPresent());
    }
}