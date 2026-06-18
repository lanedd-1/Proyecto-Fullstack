package com.semestral.venta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.semestral.venta.client.ProductoClient;
import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.VentaRepository;
import com.semestral.venta.exception.ResourceNotFoundException;

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
    void obtenerTodas_shouldMapToDTOs() {
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setFechaV(LocalDateTime.now());
        v.setTotal(0.0);

        when(ventaRepository.findAll()).thenReturn(List.of(v));

        List<VentaResponseDTO> result = ventaService.obtenerTodas();
        assertEquals(1, result.size());
        assertEquals(v.getIdVenta(), result.get(0).getIdVenta());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_whenNotFound_shouldThrow() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ventaService.obtenerPorId(99L));
    }

    @Test
    void crearVenta_withNullFecha_shouldSaveAndReturnDTO() {
        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setFechaV(null);

        Venta saved = new Venta();
        saved.setIdVenta(10L);
        saved.setFechaV(LocalDateTime.now());
        saved.setTotal(0.0);

        when(ventaRepository.save(any(Venta.class))).thenReturn(saved);

        VentaResponseDTO res = ventaService.crearVenta(dto);
        assertEquals(10L, res.getIdVenta());
        assertEquals(0.0, res.getTotal());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void crearVenta_withInvalidFecha_shouldThrow() {
        VentaRequestDTO dto = new VentaRequestDTO("invalid-date");
        assertThrows(IllegalArgumentException.class, () -> ventaService.crearVenta(dto));
    }

}
