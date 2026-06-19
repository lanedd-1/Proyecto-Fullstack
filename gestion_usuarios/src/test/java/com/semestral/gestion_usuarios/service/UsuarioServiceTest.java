package com.semestral.gestion_usuarios.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.semestral.gestion_usuarios.client.EstadoClient;
import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.exception.BusinessConflictException;
import com.semestral.gestion_usuarios.exception.ExternalServiceException;
import com.semestral.gestion_usuarios.exception.ResourceNotFoundException;
import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.model.Usuario;
import com.semestral.gestion_usuarios.repository.RolRepository;
import com.semestral.gestion_usuarios.repository.UsuarioRepository;

import feign.FeignException;
import feign.Request;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unit de UsuarioService")
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRep;

    @Mock
    private RolRepository rolRep;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EstadoClient estadoClient;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private Rol rolEjemplo;
    private UsuarioRequestDTO requestCrear;
    private UsuarioRequestDTO requestActualizar;

    @BeforeEach
    void setUp() {
        rolEjemplo = new Rol(1L, "ADMIN");

        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setIdUsuario(1L);
        usuarioEjemplo.setNombreU("Juan Perez");
        usuarioEjemplo.setRutU("11111111-1");
        usuarioEjemplo.setCorreoU("juan@mail.com");
        usuarioEjemplo.setClaveU("hash_secreto");
        usuarioEjemplo.setRol(rolEjemplo);
        usuarioEjemplo.setIdEstado(1L);

        requestCrear = new UsuarioRequestDTO("Juan Perez", "11111111-1", "juan@mail.com", "12345", 1L, 1L);
        requestActualizar = new UsuarioRequestDTO("Juan Actualizado", "11111111-1", "nuevo@mail.com", "", 1L, 1L);
    }


    @Test
    @DisplayName("getAllUsuarios() retorna la lista de DTO de todos los usuarios")
    void getAllUsuarios_debeRetornarLista() {
        when(usuarioRep.findAll()).thenReturn(List.of(usuarioEjemplo));

        List<UsuarioResponseDTO> resultado = usuarioService.getAllUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombreU());
        verify(usuarioRep, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllUsuarios() retorna lista vacia cuando no hay registros")
    void getAllUsuarios_debeRetornarVacio_siNoHayDatos() {
        when(usuarioRep.findAll()).thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = usuarioService.getAllUsuarios();

        assertTrue(resultado.isEmpty());
    }


    @Test
    @DisplayName("findByIdOrThrow() retorna el usuario si existe")
    void findByIdOrThrow_debeRetornarUsuario_cuandoExiste() {
        when(usuarioRep.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        UsuarioResponseDTO resultado = usuarioService.findByIdOrThrow(1L);

        assertNotNull(resultado);
        assertEquals("juan@mail.com", resultado.getCorreoU());
    }

    @Test
    @DisplayName("findByIdOrThrow() lanza ResourceNotFoundException si no existe")
    void findByIdOrThrow_debeLanzarExcepcion_cuandoNoExiste() {
        when(usuarioRep.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.findByIdOrThrow(99L));
    }
    @Test
    @DisplayName("saveUsuario() crea y retorna el usuario correctamente")
    void saveUsuario_debeCrearUsuario_cuandoDatosValidos() {
        when(rolRep.findById(1L)).thenReturn(Optional.of(rolEjemplo));
        when(usuarioRep.findByCorreoU("juan@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345")).thenReturn("hash_secreto");
        when(usuarioRep.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        UsuarioResponseDTO resultado = usuarioService.saveUsuario(requestCrear);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreU());
        verify(estadoClient, times(1)).obtenerEstadoPorId(1L);
        verify(usuarioRep, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("saveUsuario() lanza DataIntegrityViolationException por correo duplicado")
    void saveUsuario_debeLanzarExcepcion_cuandoCorreoDuplicado() {
        when(rolRep.findById(1L)).thenReturn(Optional.of(rolEjemplo));
        when(usuarioRep.findByCorreoU("juan@mail.com")).thenReturn(Optional.of(usuarioEjemplo));

        assertThrows(DataIntegrityViolationException.class, () -> usuarioService.saveUsuario(requestCrear));
        verify(usuarioRep, times(0)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("saveUsuario() lanza ExternalServiceException si ms-estado falla")
    void saveUsuario_debeLanzarExcepcion_cuandoMsEstadoFalla() {
        when(rolRep.findById(1L)).thenReturn(Optional.of(rolEjemplo));
        
        // Simular que el FeignClient de Estados se cae
        when(estadoClient.obtenerEstadoPorId(1L)).thenThrow(
            new FeignException.ServiceUnavailable("ms-estados no disponible", buildDummyRequest(), null, null)
        );

        assertThrows(ExternalServiceException.class, () -> usuarioService.saveUsuario(requestCrear));
    }

    @Test
    @DisplayName("update() actualiza el usuario correctamente")
    void update_debeActualizar_cuandoDatosValidos() {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setIdUsuario(1L);
        usuarioActualizado.setNombreU("Juan Actualizado");
        usuarioActualizado.setCorreoU("nuevo@mail.com");
        usuarioActualizado.setRol(rolEjemplo);

        when(usuarioRep.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRep.findByCorreoU("nuevo@mail.com")).thenReturn(Optional.empty());
        when(rolRep.findById(1L)).thenReturn(Optional.of(rolEjemplo));
        when(usuarioRep.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        UsuarioResponseDTO resultado = usuarioService.update(1L, requestActualizar);

        assertNotNull(resultado);
        assertEquals("Juan Actualizado", resultado.getNombreU());
        assertEquals("nuevo@mail.com", resultado.getCorreoU());
    }

    @Test
    @DisplayName("update() lanza DataIntegrityViolationException si el nuevo correo está ocupado")
    void update_debeLanzarExcepcion_cuandoNuevoCorreoEstaOcupado() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setIdUsuario(2L); // ID distinto
        otroUsuario.setCorreoU("nuevo@mail.com");

        when(usuarioRep.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRep.findByCorreoU("nuevo@mail.com")).thenReturn(Optional.of(otroUsuario));

        assertThrows(DataIntegrityViolationException.class, () -> usuarioService.update(1L, requestActualizar));
        verify(usuarioRep, times(0)).save(any(Usuario.class));
    }


    @Test
    @DisplayName("loginDirecto() retorna usuario con credenciales correctas")
    void loginDirecto_debeRetornarUsuario_cuandoCredencialesCorrectas() {
        when(usuarioRep.findByCorreoU("juan@mail.com")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("12345", "hash_secreto")).thenReturn(true);

        UsuarioResponseDTO resultado = usuarioService.loginDirecto("juan@mail.com", "12345");

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdU());
    }

    @Test
    @DisplayName("loginDirecto() lanza BusinessConflictException con clave incorrecta")
    void loginDirecto_debeLanzarExcepcion_cuandoClaveIncorrecta() {
        when(usuarioRep.findByCorreoU("juan@mail.com")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("claveMala", "hash_secreto")).thenReturn(false);

        assertThrows(BusinessConflictException.class, () -> usuarioService.loginDirecto("juan@mail.com", "claveMala"));
    }

    private Request buildDummyRequest() {
        return Request.create(Request.HttpMethod.GET, "/api/estados",
                java.util.Collections.emptyMap(), Request.Body.empty(), null);
    }
}