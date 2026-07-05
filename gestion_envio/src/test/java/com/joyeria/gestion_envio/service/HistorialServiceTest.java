package com.joyeria.gestion_envio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joyeria.gestion_envio.client.UsuarioClient;
import com.joyeria.gestion_envio.dto.HistorialRequestDTO;
import com.joyeria.gestion_envio.dto.HistorialResponseDTO;
import com.joyeria.gestion_envio.exception.BusinessConflictException;
import com.joyeria.gestion_envio.exception.ExternalServiceException;
import com.joyeria.gestion_envio.exception.ResourceNotFoundException;
import com.joyeria.gestion_envio.model.Historial;
import com.joyeria.gestion_envio.repository.EnvioRepository;
import com.joyeria.gestion_envio.repository.HistorialRepository;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unitario de HistorialService")
public class HistorialServiceTest {

    @Mock
    private HistorialRepository historialRep;

    @Mock
    private EnvioRepository envioRep;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private HistorialService historialService;

    private Historial historial;
    private HistorialRequestDTO requestCrear;

    @BeforeEach
    void setUp() {
        historial = new Historial();
        historial.setIdHistorial(1L);
        historial.setIdEnvio(10L);
        historial.setFecha(LocalDateTime.now());
        historial.setEstado("ENTREGADO");
        historial.setIdUsuario(5L);

        requestCrear = new HistorialRequestDTO();
        requestCrear.setIdEnvio(10L);
        requestCrear.setEstado("ENTREGADO");
        requestCrear.setIdUsuario(5L);
    }

    @Test
    @DisplayName("getAllHistorial() retorna todos los registros")
    void getAllHistorial_debeRetornarLista() {
        when(historialRep.findAll()).thenReturn(List.of(historial));

        List<HistorialResponseDTO> resultado = historialService.getAllHistorial();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("getHistorialByEnvioId() retorna lista filtrada por envío")
    void getHistorialByEnvioId_debeRetornarLista_cuandoEnvioExiste() {
        when(envioRep.existsById(10L)).thenReturn(true);
        when(historialRep.findByIdEnvio(10L)).thenReturn(List.of(historial));

        List<HistorialResponseDTO> resultado = historialService.getHistorialByEnvioId(10L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("ENTREGADO", resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("getHistorialByEnvioId() lanza excepción si el envío no existe")
    void getHistorialByEnvioId_debeLanzarExcepcion_cuandoEnvioNoExiste() {
        when(envioRep.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> historialService.getHistorialByEnvioId(99L));
    }

    @Test
    @DisplayName("findByIdOrThrow() retorna registro de historial si existe")
    void findByIdOrThrow_debeRetornarHistorial_cuandoExiste() {
        when(historialRep.findById(1L)).thenReturn(Optional.of(historial));

        HistorialResponseDTO resultado = historialService.findByIdOrThrow(1L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getIdEnvio());
    }
    @Test
    @DisplayName("saveHistorial() registra exitosamente el movimiento")
    void saveHistorial_debeGuardar_cuandoDatosSonValidos() {
        when(envioRep.existsById(10L)).thenReturn(true);
        when(historialRep.save(any(Historial.class))).thenReturn(historial);

        HistorialResponseDTO resultado = historialService.saveHistorial(requestCrear);

        assertNotNull(resultado);
        assertEquals("ENTREGADO", resultado.getEstado());
        verify(usuarioClient, times(1)).obtenerUsuarioPorId(5L);
        verify(historialRep, times(1)).save(any(Historial.class));
    }

    @Test
    @DisplayName("saveHistorial() lanza excepción si faltan datos obligatorios")
    void saveHistorial_debeLanzarExcepcion_cuandoFaltanDatos() {
        HistorialRequestDTO reqIncompleto = new HistorialRequestDTO();
        reqIncompleto.setIdEnvio(10L);

        assertThrows(BusinessConflictException.class, () -> historialService.saveHistorial(reqIncompleto));
        verify(historialRep, times(0)).save(any());
    }

    @Test
    @DisplayName("saveHistorial() lanza excepción si el usuario responsable no existe")
    void saveHistorial_debeLanzarExcepcion_cuandoUsuarioNoExiste() {
        when(envioRep.existsById(10L)).thenReturn(true);
        
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(usuarioClient.obtenerUsuarioPorId(5L)).thenThrow(notFound);

        assertThrows(ResourceNotFoundException.class, () -> historialService.saveHistorial(requestCrear));
        verify(historialRep, times(0)).save(any());
    }
    
    @Test
    @DisplayName("saveHistorial() lanza ExternalServiceException si ms-usuarios falla")
    void saveHistorial_debeLanzarExcepcion_cuandoMsUsuariosSeCae() {
        when(envioRep.existsById(10L)).thenReturn(true);
        
        FeignException serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(usuarioClient.obtenerUsuarioPorId(5L)).thenThrow(serviceUnavailable);

        assertThrows(ExternalServiceException.class, () -> historialService.saveHistorial(requestCrear));
        verify(historialRep, times(0)).save(any());
    }

    @Test
    @DisplayName("saveHistorial() lanza excepción si el envío referenciado no existe")
    void saveHistorial_debeLanzarExcepcion_cuandoEnvioNoExiste() {
        when(envioRep.existsById(10L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> historialService.saveHistorial(requestCrear));
        verify(historialRep, times(0)).save(any());
    }
}