package com.semestral.gestion_usuarios.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.repository.RolRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unit de RolService")
public class RolServiceTest {

    @Mock
    private RolRepository rolRep;

    @InjectMocks
    private RolService rolService;

    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(1L, "ADMIN");
    }

    @Test
    @DisplayName("findAll() retorna la lista completa de roles")
    void findAll_debeRetornarListaDeRoles() {
        when(rolRep.findAll()).thenReturn(List.of(rolAdmin));

        List<Rol> resultado = rolService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(rolRep, times(1)).findAll();
    }

    @Test
    @DisplayName("getById() retorna el rol cuando existe")
    void getById_debeRetornarRol_cuandoExiste() {
        when(rolRep.findById(1L)).thenReturn(Optional.of(rolAdmin));

        Rol resultado = rolService.getById(1L);

        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombreRol());
    }

    @Test
    @DisplayName("getById() lanza RuntimeException cuando no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        when(rolRep.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rolService.getById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("findByNombre() retorna el rol cuando el nombre coincide exactamente")
    void findByNombre_debeRetornarRol_cuandoCoincide() {
        when(rolRep.findByNombreRol("ADMIN")).thenReturn(Optional.of(rolAdmin));

        Optional<Rol> resultado = rolService.findByNombre("ADMIN");

        assertTrue(resultado.isPresent());
        assertEquals("ADMIN", resultado.get().getNombreRol());
    }

    @Test
    @DisplayName("findByNombre() retorna vacio cuando no hay coincidencia")
    void findByNombre_debeRetornarVacio_cuandoNoCoincide() {
        when(rolRep.findByNombreRol("INEXISTENTE")).thenReturn(Optional.empty());

        Optional<Rol> resultado = rolService.findByNombre("INEXISTENTE");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("findByNombreIgnoreCase() retorna el rol ignorando mayusculas/minusculas")
    void findByNombreIgnoreCase_debeRetornarRol() {
        when(rolRep.findByNombreRolIgnoreCase("admin")).thenReturn(Optional.of(rolAdmin));

        Optional<Rol> resultado = rolService.findByNombreIgnoreCase("admin");

        assertTrue(resultado.isPresent());
        assertEquals("ADMIN", resultado.get().getNombreRol());
    }

    @Test
    @DisplayName("create() guarda el rol cuando el nombre no existe")
    void create_debeGuardarRol_cuandoNombreNoExiste() {
        Rol nuevo = new Rol(null, "SUPERVISOR");
        Rol guardado = new Rol(2L, "SUPERVISOR");

        when(rolRep.findByNombreRolIgnoreCase("SUPERVISOR")).thenReturn(Optional.empty());
        when(rolRep.save(nuevo)).thenReturn(guardado);

        Rol resultado = rolService.create(nuevo);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getIdRol());
        verify(rolRep, times(1)).save(nuevo);
    }

    @Test
    @DisplayName("create() lanza DataIntegrityViolationException si el nombre ya existe")
    void create_debeLanzarExcepcion_cuandoNombreYaExiste() {
        Rol duplicado = new Rol(null, "ADMIN");

        when(rolRep.findByNombreRolIgnoreCase("ADMIN")).thenReturn(Optional.of(rolAdmin));

        assertThrows(DataIntegrityViolationException.class, () -> rolService.create(duplicado));
        verify(rolRep, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("update() actualiza el nombre cuando el rol existe y el nuevo nombre esta libre")
    void update_debeActualizarNombre_cuandoEstaLibre() {
        Rol cambios = new Rol(null, "SUPER_ADMIN");
        Rol actualizado = new Rol(1L, "SUPER_ADMIN");

        when(rolRep.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(rolRep.findByNombreRolIgnoreCase("SUPER_ADMIN")).thenReturn(Optional.empty());
        when(rolRep.save(any(Rol.class))).thenReturn(actualizado);

        Rol resultado = rolService.update(1L, cambios);

        assertNotNull(resultado);
        assertEquals("SUPER_ADMIN", resultado.getNombreRol());
    }

    @Test
    @DisplayName("update() no cambia el nombre si es el mismo (ignorando mayusculas)")
    void update_noDebeChocar_cuandoNombreEsElMismo() {
        Rol cambios = new Rol(null, "admin");

        when(rolRep.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(rolRep.save(any(Rol.class))).thenReturn(rolAdmin);

        Rol resultado = rolService.update(1L, cambios);

        assertNotNull(resultado);
        verify(rolRep, never()).findByNombreRolIgnoreCase(any());
    }

    @Test
    @DisplayName("update() lanza RuntimeException si el rol a actualizar no existe")
    void update_debeLanzarExcepcion_cuandoRolNoExiste() {
        Rol cambios = new Rol(null, "X");
        when(rolRep.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rolService.update(99L, cambios));
        verify(rolRep, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("update() lanza DataIntegrityViolationException si el nuevo nombre ya lo tiene otro rol")
    void update_debeLanzarExcepcion_cuandoNuevoNombreOcupado() {
        Rol cambios = new Rol(null, "USER");
        Rol otroRol = new Rol(2L, "USER");

        when(rolRep.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(rolRep.findByNombreRolIgnoreCase("USER")).thenReturn(Optional.of(otroRol));

        assertThrows(DataIntegrityViolationException.class, () -> rolService.update(1L, cambios));
        verify(rolRep, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("update() con nombre nulo mantiene el nombre existente")
    void update_conNombreNulo_debeMantenerNombreExistente() {
        Rol cambios = new Rol(null, null);

        when(rolRep.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(rolRep.save(any(Rol.class))).thenReturn(rolAdmin);

        Rol resultado = rolService.update(1L, cambios);

        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombreRol());
    }
}
