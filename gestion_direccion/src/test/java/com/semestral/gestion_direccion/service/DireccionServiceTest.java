package com.semestral.gestion_direccion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.semestral.gestion_direccion.client.EstadoClient;
import com.semestral.gestion_direccion.client.UsuarioClient;
import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.exception.ExternalServiceException;
import com.semestral.gestion_direccion.exception.ResourceNotFoundException;
import com.semestral.gestion_direccion.model.Comuna;
import com.semestral.gestion_direccion.model.Direccion;
import com.semestral.gestion_direccion.model.Region;
import com.semestral.gestion_direccion.repository.ComunaRepository;
import com.semestral.gestion_direccion.repository.DireccionRepository;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unitario de DireccionService")
public class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRep;

    @Mock
    private ComunaRepository comunaRep;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private EstadoClient estadoClient;

    @InjectMocks
    private DireccionService direccionService;

    private Region region;
    private Comuna comuna;
    private Direccion direccion;
    private DireccionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        region = new Region(1L, "Metropolitana");
        comuna = new Comuna(1L, "Quilicura", region);

        direccion = new Direccion();
        direccion.setIdDireccion(1L);
        direccion.setCalle("Av. Siempre Viva");
        direccion.setNumero("742");
        direccion.setComuna(comuna);
        direccion.setIdUsuario(10L);
        direccion.setIdEstado(1L);

        requestDTO = new DireccionRequestDTO();
        requestDTO.setCalle("Av. Siempre Viva");
        requestDTO.setNumero("742");
        requestDTO.setIdComuna(1L);
        requestDTO.setIdUsuario(10L);
        requestDTO.setIdEstado(1L);
    }

    @Test
    @DisplayName("findAll() debe retornar la lista de DTOs de todas las direcciones")
    void findAll_debeRetornarLista() {
        when(direccionRep.findAllWithComunaAndRegion()).thenReturn(List.of(direccion));

        List<DireccionResponseDTO> resultado = direccionService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Av. Siempre Viva", resultado.get(0).getCalle());
        assertEquals("Quilicura", resultado.get(0).getComuna());
        verify(direccionRep, times(1)).findAllWithComunaAndRegion();
    }

    @Test
    @DisplayName("findAll() retorna lista vacía cuando no hay registros")
    void findAll_debeRetornarVacio() {
        when(direccionRep.findAllWithComunaAndRegion()).thenReturn(List.of());

        List<DireccionResponseDTO> resultado = direccionService.findAll();

        assertTrue(resultado.isEmpty());
    }


    @Test
    @DisplayName("findByIdOrThrow() retorna la dirección si existe")
    void findByIdOrThrow_debeRetornarDireccion_cuandoExiste() {
        when(direccionRep.findByIdWithComunaAndRegion(1L)).thenReturn(Optional.of(direccion));

        DireccionResponseDTO resultado = direccionService.findByIdOrThrow(1L);

        assertNotNull(resultado);
        assertEquals("Av. Siempre Viva", resultado.getCalle());
    }

    @Test
    @DisplayName("findByIdOrThrow() lanza ResourceNotFoundException si no existe")
    void findByIdOrThrow_debeLanzarExcepcion_cuandoNoExiste() {
        when(direccionRep.findByIdWithComunaAndRegion(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> direccionService.findByIdOrThrow(99L));
    }

    @Test
    @DisplayName("create() guarda y retorna la dirección correctamente")
    void create_debeGuardarDireccion_cuandoDatosValidos() {
        when(comunaRep.findById(1L)).thenReturn(Optional.of(comuna));
        when(direccionRep.save(any(Direccion.class))).thenReturn(direccion);

        DireccionResponseDTO resultado = direccionService.create(requestDTO);

        assertNotNull(resultado);
        assertEquals("Av. Siempre Viva", resultado.getCalle());
        verify(usuarioClient, times(1)).obtenerUsuarioPorId(10L);
        verify(estadoClient, times(1)).obtenerEstadoPorId(1L);
        verify(direccionRep, times(1)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("create() lanza ResourceNotFoundException si la comuna no existe")
    void create_debeLanzarExcepcion_cuandoComunaNoExiste() {
        when(comunaRep.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> direccionService.create(requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("create() lanza ResourceNotFoundException si el usuario (Feign) no existe")
    void create_debeLanzarExcepcion_cuandoUsuarioNoExisteEnMsUsuarios() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenThrow(notFoundException);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.create(requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("create() lanza ExternalServiceException si ms-usuarios se cae (503)")
    void create_debeLanzarExcepcion_cuandoMsUsuariosFalla() {
        FeignException serviceUnavailableException = mock(FeignException.ServiceUnavailable.class);
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenThrow(serviceUnavailableException);

        assertThrows(ExternalServiceException.class, () -> direccionService.create(requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("update() actualiza la dirección correctamente")
    void update_debeActualizarDireccion_cuandoDatosValidos() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        when(comunaRep.findById(1L)).thenReturn(Optional.of(comuna));
        when(direccionRep.save(any(Direccion.class))).thenReturn(direccion);

        DireccionRequestDTO updateReq = new DireccionRequestDTO();
        updateReq.setCalle("Av. Nueva");
        updateReq.setNumero("999");
        updateReq.setIdComuna(1L);
        updateReq.setIdUsuario(10L);
        updateReq.setIdEstado(1L);

        DireccionResponseDTO resultado = direccionService.update(1L, updateReq);

        assertNotNull(resultado);
        verify(direccionRep, times(1)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("delete() elimina la dirección si existe")
    void delete_debeEliminar_cuandoExiste() {
        when(direccionRep.existsById(1L)).thenReturn(true);

        direccionService.delete(1L);

        verify(direccionRep, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete() lanza ResourceNotFoundException si no existe")
    void delete_debeLanzarExcepcion_cuandoNoExiste() {
        when(direccionRep.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.delete(99L));
        verify(direccionRep, times(0)).deleteById(any());
    }

    @Test
    @DisplayName("create() lanza RuntimeException si idComuna es nulo")
    void create_debeLanzarExcepcion_cuandoIdComunaEsNulo() {
        requestDTO.setIdComuna(null);

        assertThrows(RuntimeException.class, () -> direccionService.create(requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("create() lanza ResourceNotFoundException si el estado (Feign) no existe")
    void create_debeLanzarExcepcion_cuandoEstadoNoExiste() {
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(estadoClient.obtenerEstadoPorId(1L)).thenThrow(notFound);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.create(requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("create() lanza ExternalServiceException si ms-estados se cae")
    void create_debeLanzarExcepcion_cuandoMsEstadosFalla() {
        FeignException serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(estadoClient.obtenerEstadoPorId(1L)).thenThrow(serviceUnavailable);

        assertThrows(ExternalServiceException.class, () -> direccionService.create(requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("create() no valida usuario/estado externos cuando idUsuario e idEstado son nulos")
    void create_noDebeLlamarClientes_cuandoIdsExternosSonNulos() {
        requestDTO.setIdUsuario(null);
        requestDTO.setIdEstado(null);

        when(comunaRep.findById(1L)).thenReturn(Optional.of(comuna));
        when(direccionRep.save(any(Direccion.class))).thenReturn(direccion);

        DireccionResponseDTO resultado = direccionService.create(requestDTO);

        assertNotNull(resultado);
        verify(usuarioClient, times(0)).obtenerUsuarioPorId(any());
        verify(estadoClient, times(0)).obtenerEstadoPorId(any());
    }

    @Test
    @DisplayName("update() lanza ResourceNotFoundException si la direccion no existe")
    void update_debeLanzarExcepcion_cuandoDireccionNoExiste() {
        when(direccionRep.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> direccionService.update(99L, requestDTO));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("update() lanza ResourceNotFoundException si la nueva comuna no existe")
    void update_debeLanzarExcepcion_cuandoNuevaComunaNoExiste() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        when(comunaRep.findById(99L)).thenReturn(Optional.empty());

        DireccionRequestDTO req = new DireccionRequestDTO();
        req.setIdComuna(99L);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.update(1L, req));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("update() lanza ResourceNotFoundException si el nuevo usuario (Feign) no existe")
    void update_debeLanzarExcepcion_cuandoNuevoUsuarioNoExiste() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(usuarioClient.obtenerUsuarioPorId(20L)).thenThrow(notFound);

        DireccionRequestDTO req = new DireccionRequestDTO();
        req.setIdUsuario(20L);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.update(1L, req));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("update() lanza ExternalServiceException si ms-usuarios falla al actualizar")
    void update_debeLanzarExcepcion_cuandoMsUsuariosFallaAlActualizar() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        FeignException serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(usuarioClient.obtenerUsuarioPorId(20L)).thenThrow(serviceUnavailable);

        DireccionRequestDTO req = new DireccionRequestDTO();
        req.setIdUsuario(20L);

        assertThrows(ExternalServiceException.class, () -> direccionService.update(1L, req));
    }

    @Test
    @DisplayName("update() lanza ResourceNotFoundException si el nuevo estado (Feign) no existe")
    void update_debeLanzarExcepcion_cuandoNuevoEstadoNoExiste() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(estadoClient.obtenerEstadoPorId(7L)).thenThrow(notFound);

        DireccionRequestDTO req = new DireccionRequestDTO();
        req.setIdEstado(7L);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.update(1L, req));
        verify(direccionRep, times(0)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("update() lanza ExternalServiceException si ms-estados falla al actualizar")
    void update_debeLanzarExcepcion_cuandoMsEstadosFallaAlActualizar() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        FeignException serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(estadoClient.obtenerEstadoPorId(7L)).thenThrow(serviceUnavailable);

        DireccionRequestDTO req = new DireccionRequestDTO();
        req.setIdEstado(7L);

        assertThrows(ExternalServiceException.class, () -> direccionService.update(1L, req));
    }

    @Test
    @DisplayName("update() con campos nulos no modifica calle ni numero")
    void update_conCamposNulos_noDebeModificarCalleNiNumero() {
        when(direccionRep.findById(1L)).thenReturn(Optional.of(direccion));
        when(direccionRep.save(any(Direccion.class))).thenReturn(direccion);

        DireccionRequestDTO req = new DireccionRequestDTO();

        DireccionResponseDTO resultado = direccionService.update(1L, req);

        assertNotNull(resultado);
        assertEquals("Av. Siempre Viva", resultado.getCalle());
    }
}