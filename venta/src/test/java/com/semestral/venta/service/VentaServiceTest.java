package com.semestral.venta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.semestral.venta.client.ProductoClient;
import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.exception.ResourceNotFoundException;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.VentaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de VentaService")
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void obtenerTodas_shouldReturnListOfVentas() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.now());
        v.setTotal(0.0);
        v.setDetalles(new ArrayList<>());

        when(ventaRepository.findAll()).thenReturn(List.of(v));

        List<VentaResponseDTO> result = ventaService.obtenerTodas();
        assertEquals(1, result.size());
        assertEquals(v.getIdVenta(), result.get(0).getIdVenta());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodas_shouldReturnEmptyList() {
        when(ventaRepository.findAll()).thenReturn(new ArrayList<>());

        List<VentaResponseDTO> result = ventaService.obtenerTodas();
        assertEquals(0, result.size());
    }

    @Test
    void obtenerPorId_shouldReturnVenta_whenExists() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.now());
        v.setTotal(100.0);
        v.setDetalles(new ArrayList<>());

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(v));

        VentaResponseDTO result = ventaService.obtenerPorId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdVenta());
        assertEquals(100.0, result.getTotal());
    }

    @Test
    void obtenerPorId_shouldThrowException_whenNotFound() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ventaService.obtenerPorId(99L));
    }

    @Test
    void crearVenta_withValidFecha_shouldSaveAndReturnDTO() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV("2024-06-15T14:30:00");

        Venta saved = new Venta();
        saved.setIdVenta(10L);
        saved.setFechaV(LocalDateTime.parse("2024-06-15T14:30:00"));
        saved.setTotal(0.0);
        saved.setDetalles(new ArrayList<>());

        when(ventaRepository.save(any(Venta.class))).thenReturn(saved);

        VentaResponseDTO res = ventaService.crearVenta(dto);
        assertNotNull(res);
        assertEquals(10L, res.getIdVenta());
        assertEquals(0.0, res.getTotal());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void crearVenta_withNullFecha_shouldThrowException() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV(null);

        assertThrows(IllegalArgumentException.class, () -> ventaService.crearVenta(dto));
    }

    @Test
    void crearVenta_withInvalidFecha_shouldThrowException() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV("fechaInvalida-date");

        assertThrows(Exception.class, () -> ventaService.crearVenta(dto));
    }

    @Test
    void crearVenta_withMalformedFecha_shouldThrowException() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV("15-06-2024");

        assertThrows(Exception.class, () -> ventaService.crearVenta(dto));
    }

    @Test
    void convertToDTO_shouldMapVentaCorrectly() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.parse("2024-06-15T14:30:00"));
        v.setTotal(150.0);
        v.setDetalles(new ArrayList<>());

        VentaResponseDTO dto = ventaService.convertToDTO(v);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdVenta());
        assertEquals(150.0, dto.getTotal());
        assertNotNull(dto.getFechaV());
    }

    @Test
    void convertToDTO_withNullFecha_shouldMapCorrectly() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(null);
        v.setTotal(0.0);
        v.setDetalles(new ArrayList<>());

        VentaResponseDTO dto = ventaService.convertToDTO(v);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdVenta());
        assertEquals(0.0, dto.getTotal());
    }

    @Test
    void convertToDTO_withNullDetalles_shouldMapCorrectly() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.now());
        v.setTotal(0.0);
        v.setDetalles(null);

        VentaResponseDTO dto = ventaService.convertToDTO(v);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdVenta());
        assertNotNull(dto.getDetalles());
        assertEquals(0, dto.getDetalles().size());
    }

    @Test
    void obtenerPorId_shouldLogWarning_whenNotFound() {
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            ventaService.obtenerPorId(999L);
        } catch (ResourceNotFoundException e) {
            assertEquals("Recurso no encontrado con ID: 999", e.getMessage());
        }
    }

    @Test
    void convertToDTO_withNullTotal_shouldDefault() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.now());
        v.setTotal(null);
        v.setDetalles(new ArrayList<>());

        VentaResponseDTO dto = ventaService.convertToDTO(v);

        assertNotNull(dto);
        assertEquals(0.0, dto.getTotal());
    }

    @Test
    void crearVenta_shouldSetTotalToZero() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV("2024-06-15T14:30:00");

        Venta saved = new Venta();
        saved.setIdVenta(10L);
        saved.setFechaV(LocalDateTime.parse("2024-06-15T14:30:00"));
        saved.setTotal(0.0);
        saved.setDetalles(new ArrayList<>());

        when(ventaRepository.save(any(Venta.class))).thenReturn(saved);

        VentaResponseDTO res = ventaService.crearVenta(dto);

        assertNotNull(res);
        assertEquals(0.0, res.getTotal());
    }

    @Test
    void obtenerTodas_shouldMapAllVentas() {
        Venta v1 = new Venta();
        v1.setIdVenta(1L);
        v1.setFechaV(LocalDateTime.now());
        v1.setTotal(100.0);
        v1.setDetalles(new ArrayList<>());

        Venta v2 = new Venta();
        v2.setIdVenta(2L);
        v2.setFechaV(LocalDateTime.now());
        v2.setTotal(200.0);
        v2.setDetalles(new ArrayList<>());

        when(ventaRepository.findAll()).thenReturn(java.util.List.of(v1, v2));

        List<VentaResponseDTO> result = ventaService.obtenerTodas();
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getTotal());
        assertEquals(200.0, result.get(1).getTotal());
    }

    @Test
    void crearVenta_withBlankFecha_shouldUseCurrent() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV("   ");

        assertThrows(IllegalArgumentException.class, () -> ventaService.crearVenta(dto));
    }

    @Test
    void obtenerPorId_shouldReturnVentaWithDetalles() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.now());
        v.setTotal(150.0);
        
        java.util.List<com.semestral.venta.model.Detalle> detalles = new ArrayList<>();
        v.setDetalles(detalles);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(v));

        VentaResponseDTO result = ventaService.obtenerPorId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdVenta());
        assertEquals(150.0, result.getTotal());
        assertNotNull(result.getDetalles());
    }
}

