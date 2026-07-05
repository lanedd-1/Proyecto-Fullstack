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

import com.joyeria.gestion_envio.client.DireccionClient;
import com.joyeria.gestion_envio.client.VentaClient;
import com.joyeria.gestion_envio.dto.EnvioRequestDTO;
import com.joyeria.gestion_envio.dto.EnvioResponseDTO;
import com.joyeria.gestion_envio.exception.BusinessConflictException;
import com.joyeria.gestion_envio.exception.ExternalServiceException;
import com.joyeria.gestion_envio.exception.ResourceNotFoundException;
import com.joyeria.gestion_envio.model.Envio;
import com.joyeria.gestion_envio.repository.EnvioRepository;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unitario de EnvioService")
public class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRep;

    @Mock
    private VentaClient ventaClient;

    @Mock
    private DireccionClient direccionClient;

    @InjectMocks
    private EnvioService envioService;

    private Envio envio;
    private EnvioRequestDTO requestCrear;

    @BeforeEach
    void setUp() {
        envio = new Envio();
        envio.setIdEnvio(1L);
        envio.setFechaEnvio(LocalDateTime.now().minusDays(1));
        envio.setFechaRecep(LocalDateTime.now().plusDays(2));
        envio.setIdVenta(50L);
        envio.setIdDireccion(10L);
        envio.setEstado("PREPARACION");

        requestCrear = new EnvioRequestDTO();
        requestCrear.setIdVenta(50L);
        requestCrear.setIdDireccion(10L);
        requestCrear.setEstado("PREPARACION");
    }

    // ==========================================
    // TEST UNIT - getAllEnvios() & findById()
    // ==========================================
    @Test
    @DisplayName("getAllEnvios() retorna lista de DTOs")
    void getAllEnvios_debeRetornarLista() {
        when(envioRep.findAll()).thenReturn(List.of(envio));

        List<EnvioResponseDTO> resultado = envioService.getAllEnvios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PREPARACION", resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("findByIdOrThrow() retorna envío si existe")
    void findByIdOrThrow_debeRetornarEnvio_cuandoExiste() {
        when(envioRep.findById(1L)).thenReturn(Optional.of(envio));

        EnvioResponseDTO resultado = envioService.findByIdOrThrow(1L);

        assertNotNull(resultado);
        assertEquals(50L, resultado.getIdVenta());
    }

    @Test
    @DisplayName("findByIdOrThrow() lanza excepción si no existe")
    void findByIdOrThrow_debeLanzarExcepcion_cuandoNoExiste() {
        when(envioRep.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> envioService.findByIdOrThrow(99L));
    }

    // ==========================================
    // TEST UNIT - saveEnvio()
    // ==========================================
    @Test
    @DisplayName("saveEnvio() crea el envío validando con los clientes Feign")
    void saveEnvio_debeCrear_cuandoDatosYClientesSonValidos() {
        when(envioRep.save(any(Envio.class))).thenReturn(envio);
        // Los clientes Feign (ventaClient y direccionClient) retornan void/Response sin excepción, simulando éxito.

        EnvioResponseDTO resultado = envioService.saveEnvio(requestCrear);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEnvio());
        verify(ventaClient, times(1)).obtenerVentaPorId(50L);
        verify(direccionClient, times(1)).obtenerDireccionPorId(10L);
        verify(envioRep, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() lanza ResourceNotFoundException si la Venta no existe (404 Feign)")
    void saveEnvio_debeLanzarExcepcion_cuandoVentaNoExiste() {
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(ventaClient.obtenerVentaPorId(50L)).thenThrow(notFound);

        assertThrows(ResourceNotFoundException.class, () -> envioService.saveEnvio(requestCrear));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() lanza ExternalServiceException si Dirección falla (503 Feign)")
    void saveEnvio_debeLanzarExcepcion_cuandoMsDireccionSeCae() {
        FeignException serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(direccionClient.obtenerDireccionPorId(10L)).thenThrow(serviceUnavailable);

        assertThrows(ExternalServiceException.class, () -> envioService.saveEnvio(requestCrear));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    // ==========================================
    // TEST UNIT - update()
    // ==========================================
    @Test
    @DisplayName("update() actualiza el envío correctamente")
    void update_debeActualizar_cuandoDatosValidos() {
        when(envioRep.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRep.save(any(Envio.class))).thenReturn(envio);

        EnvioRequestDTO reqUpdate = new EnvioRequestDTO();
        reqUpdate.setEstado("EN_CAMINO");
        reqUpdate.setFechaRecepcion(LocalDateTime.now().plusDays(5));

        EnvioResponseDTO resultado = envioService.update(1L, reqUpdate);

        assertNotNull(resultado);
        verify(envioRep, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("update() lanza BusinessConflictException si fecha recepción es antes que envío")
    void update_debeLanzarExcepcion_cuandoConflictoDeFechas() {
        when(envioRep.findById(1L)).thenReturn(Optional.of(envio)); // fecha envio fue ayer

        EnvioRequestDTO reqUpdate = new EnvioRequestDTO();
        // Intentamos setear fecha de recepción hace 5 días (antes del envío)
        reqUpdate.setFechaRecepcion(LocalDateTime.now().minusDays(5)); 

        assertThrows(BusinessConflictException.class, () -> envioService.update(1L, reqUpdate));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() lanza BusinessConflictException si idVenta es nulo")
    void saveEnvio_debeLanzarExcepcion_cuandoIdVentaEsNulo() {
        requestCrear.setIdVenta(null);

        assertThrows(BusinessConflictException.class, () -> envioService.saveEnvio(requestCrear));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() lanza BusinessConflictException si idDireccion es nulo")
    void saveEnvio_debeLanzarExcepcion_cuandoIdDireccionEsNulo() {
        requestCrear.setIdDireccion(null);

        assertThrows(BusinessConflictException.class, () -> envioService.saveEnvio(requestCrear));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() lanza ExternalServiceException si ms-ventas no responde")
    void saveEnvio_debeLanzarExcepcion_cuandoMsVentasSeCae() {
        FeignException serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(ventaClient.obtenerVentaPorId(50L)).thenThrow(serviceUnavailable);

        assertThrows(ExternalServiceException.class, () -> envioService.saveEnvio(requestCrear));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() lanza ResourceNotFoundException si la Direccion no existe (404 Feign)")
    void saveEnvio_debeLanzarExcepcion_cuandoDireccionNoExiste() {
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(direccionClient.obtenerDireccionPorId(10L)).thenThrow(notFound);

        assertThrows(ResourceNotFoundException.class, () -> envioService.saveEnvio(requestCrear));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("saveEnvio() aplica valores por defecto cuando fechas y estado no son enviados")
    void saveEnvio_debeAplicarValoresPorDefecto_cuandoNoSeEnvian() {
        EnvioRequestDTO reqSinDefaults = new EnvioRequestDTO();
        reqSinDefaults.setIdVenta(50L);
        reqSinDefaults.setIdDireccion(10L);
        // fechaEnvio, fechaRecepcion y estado quedan nulos

        when(envioRep.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));

        EnvioResponseDTO resultado = envioService.saveEnvio(reqSinDefaults);

        assertNotNull(resultado);
        assertEquals("PREPARACION", resultado.getEstado());
        assertNotNull(resultado.getFechaEnvio());
        assertNotNull(resultado.getFechaRecepcion());
    }

    @Test
    @DisplayName("update() lanza ResourceNotFoundException si el envio no existe")
    void update_debeLanzarExcepcion_cuandoEnvioNoExiste() {
        when(envioRep.findById(99L)).thenReturn(Optional.empty());

        EnvioRequestDTO reqUpdate = new EnvioRequestDTO();
        reqUpdate.setEstado("EN_CAMINO");

        assertThrows(ResourceNotFoundException.class, () -> envioService.update(99L, reqUpdate));
        verify(envioRep, times(0)).save(any(Envio.class));
    }

    @Test
    @DisplayName("update() actualiza idVenta, idDireccion y fechaEnvio cuando se envian")
    void update_debeActualizarIdVentaIdDireccionYFecha_cuandoSeEnvian() {
        when(envioRep.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRep.save(any(Envio.class))).thenReturn(envio);

        EnvioRequestDTO reqUpdate = new EnvioRequestDTO();
        reqUpdate.setIdVenta(99L);
        reqUpdate.setIdDireccion(88L);
        reqUpdate.setFechaEnvio(LocalDateTime.now());

        EnvioResponseDTO resultado = envioService.update(1L, reqUpdate);

        assertNotNull(resultado);
        verify(envioRep, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("update() no modifica el estado si viene en blanco")
    void update_noDebeModificarEstado_cuandoVieneEnBlanco() {
        when(envioRep.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRep.save(any(Envio.class))).thenReturn(envio);

        EnvioRequestDTO reqUpdate = new EnvioRequestDTO();
        reqUpdate.setEstado("   ");

        EnvioResponseDTO resultado = envioService.update(1L, reqUpdate);

        assertNotNull(resultado);
        assertEquals("PREPARACION", resultado.getEstado());
    }
}