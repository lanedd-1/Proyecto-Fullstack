package com.joyeria.gestionestado.Service;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joyeria.gestionestado.client.EnvioClient;
import com.joyeria.gestionestado.dto.EnvioResponseDTO;
import com.joyeria.gestionestado.dto.EstadoConEnviosResponseDTO;
import com.joyeria.gestionestado.dto.EstadoRequestDTO;
import com.joyeria.gestionestado.dto.EstadoResponseDTO;
import com.joyeria.gestionestado.exception.EstadoDuplicadoException;
import com.joyeria.gestionestado.exception.EstadoNotFoundException;
import com.joyeria.gestionestado.model.Estado;
import com.joyeria.gestionestado.repository.EstadoRepository;

import feign.FeignException;
import feign.Request;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unit de EstadoService")
public class EstadoServiceTest {


    // crear un mock del repositorio
    // debido a que no tenemos comunicación con dicho elemento a causa del Test
    @Mock
    private EstadoRepository estadoRepository;

    // crear un mock del Feign Client de ms-envio
    @Mock
    private EnvioClient envioClient;

    // crear una instancia REAL de EstadoService, inyectando los mocks anteriores
    @InjectMocks
    private EstadoService estadoService;

    // Variables para datos de pruebas reutilizables entre TEST
    private Estado estadoEjemplo;
    private EstadoRequestDTO dtoPrueba;

    @BeforeEach
    void setUp() {
        estadoEjemplo = new Estado(1L, "En Camino");
        dtoPrueba = new EstadoRequestDTO(null, "En Camino");
    }

    // TEST UNIT - obtenerTodos()

    @Test
    @DisplayName("obtenerTodos() debe retornar la lista de DTO de todos los estados")
    void obtenerTodos_debeRetornarListaDeEstados() {
        when(estadoRepository.findAll()).thenReturn(List.of(estadoEjemplo));

        List<EstadoResponseDTO> resultado = estadoService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("En Camino", resultado.get(0).getNombreEstado());

        verify(estadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar lista vacia cuando no hay estados en la BD")
    void obtenerTodos_debeRetornarListaVacia_siNoHayEstados() {
        when(estadoRepository.findAll()).thenReturn(List.of());

        List<EstadoResponseDTO> resultado = estadoService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // TEST UNIT - obtenerPorId()

    @Test
    @DisplayName("obtenerPorId() debe retornar el estado cuando existe")
    void obtenerPorId_debeRetornarEstado_cuandoExiste() {
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplo));

        EstadoResponseDTO resultado = estadoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("En Camino", resultado.getNombreEstado());
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar EstadoNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EstadoNotFoundException.class, () -> estadoService.obtenerPorId(99L));
    }

    // TEST UNIT - obtenerConEnvios()

    @Test
    @DisplayName("obtenerConEnvios() debe retornar el estado con sus envios filtrados desde ms-envio")
    void obtenerConEnvios_debeRetornarEnviosFiltrados() {
        EnvioResponseDTO envioMismoEstado = new EnvioResponseDTO(1L, "2026-01-01", null, 10L, 5L, "En Camino");
        EnvioResponseDTO envioOtroEstado = new EnvioResponseDTO(2L, "2026-01-02", null, 11L, 6L, "Entregado");

        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplo));
        when(envioClient.obtenerTodos()).thenReturn(List.of(envioMismoEstado, envioOtroEstado));

        EstadoConEnviosResponseDTO resultado = estadoService.obtenerConEnvios(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.getEnvios().size());
        assertEquals(1L, resultado.getEnvios().get(0).getIdEnvio());
    }

    @Test
    @DisplayName("obtenerConEnvios() debe retornar lista vacia de envios cuando ms-envio no esta disponible")
    void obtenerConEnvios_debeRetornarListaVacia_siMsEnvioNoDisponible() {
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplo));
        when(envioClient.obtenerTodos()).thenThrow(
                new FeignException.ServiceUnavailable("ms-envio no disponible", buildDummyRequest(), null, null));

        EstadoConEnviosResponseDTO resultado = estadoService.obtenerConEnvios(1L);

        assertNotNull(resultado);
        assertTrue(resultado.getEnvios().isEmpty());
    }

    // TEST UNIT - saveEstado()

    @Test
    @DisplayName("saveEstado() debe crear el estado cuando el nombre no existe aun")
    void saveEstado_debeCrearEstado_cuandoNombreNoExiste() {
        when(estadoRepository.existsByNombreEstadoIgnoreCase("En Camino")).thenReturn(false);
        when(estadoRepository.save(org.mockito.ArgumentMatchers.any(Estado.class))).thenReturn(estadoEjemplo);

        EstadoResponseDTO resultado = estadoService.saveEstado(dtoPrueba);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEstado());
        assertEquals("En Camino", resultado.getNombreEstado());
        verify(estadoRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Estado.class));
    }

    @Test
    @DisplayName("saveEstado() debe lanzar EstadoDuplicadoException cuando el nombre ya existe")
    void saveEstado_debeLanzarExcepcion_cuandoNombreDuplicado() {
        when(estadoRepository.existsByNombreEstadoIgnoreCase("En Camino")).thenReturn(true);

        assertThrows(EstadoDuplicadoException.class, () -> estadoService.saveEstado(dtoPrueba));

        verify(estadoRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Estado.class));
    }

    // TEST UNIT - update()

    @Test
    @DisplayName("update() debe actualizar el nombre cuando el estado existe y el nuevo nombre esta libre")
    void update_debeActualizarEstado_cuandoExiste() {
        Estado actualizado = new Estado(1L, "Entregado");
        EstadoRequestDTO dtoActualizado = new EstadoRequestDTO(null, "Entregado");

        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplo));
        when(estadoRepository.findByNombreEstadoIgnoreCase("Entregado")).thenReturn(Optional.empty());
        when(estadoRepository.save(org.mockito.ArgumentMatchers.any(Estado.class))).thenReturn(actualizado);

        EstadoResponseDTO resultado = estadoService.update(1L, dtoActualizado);

        assertNotNull(resultado);
        assertEquals("Entregado", resultado.getNombreEstado());
    }

    @Test
    @DisplayName("update() debe lanzar EstadoNotFoundException cuando el ID no existe")
    void update_debeLanzarExcepcion_cuandoNoExiste() {
        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EstadoNotFoundException.class, () -> estadoService.update(99L, dtoPrueba));
    }

    @Test
    @DisplayName("update() debe lanzar EstadoDuplicadoException cuando otro estado ya tiene ese nombre")
    void update_debeLanzarExcepcion_cuandoNuevoNombreYaLoTieneOtroEstado() {
        Estado otroEstado = new Estado(2L, "Entregado");
        EstadoRequestDTO dtoActualizado = new EstadoRequestDTO(null, "Entregado");

        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplo));
        when(estadoRepository.findByNombreEstadoIgnoreCase("Entregado")).thenReturn(Optional.of(otroEstado));

        assertThrows(EstadoDuplicadoException.class, () -> estadoService.update(1L, dtoActualizado));
    }

    // --- AUXILIAR para simular fallos de Feign ---
    private Request buildDummyRequest() {
        return Request.create(Request.HttpMethod.GET, "/api/envios",
                java.util.Collections.emptyMap(), Request.Body.empty(), null);
    }
}
