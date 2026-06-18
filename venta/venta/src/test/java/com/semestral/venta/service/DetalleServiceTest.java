package com.semestral.venta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.semestral.venta.client.ProductoClient;
import com.semestral.venta.dto.DetalleRequestDTO;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.DetalleRepository;
import com.semestral.venta.repository.VentaRepository;
import com.semestral.venta.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de DetalleService")
public class DetalleServiceTest {

    private Venta venta;

    @Mock
    private DetalleRepository detalleRe;

    @Mock
    private VentaRepository ventaRe;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private DetalleService detalleService;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setIdVenta(1L);
        venta.setFechaV(LocalDateTime.now());
        venta.setTotal(0.0);

        when(ventaRe.findById(1L)).thenReturn(Optional.of(venta));
    }

    @Test
    void crearDetalle_happyPath_updatesVentaTotalAndReturnsDTO() {

        Map<String, Object> producto = new HashMap<>();
        producto.put("idProd", 3L);
        producto.put("precio", 10.0);
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        when(detalleRe.findByIdVenta_IdVentaAndProductoId(eq(1L), eq(3L))).thenReturn(Optional.empty());

        Detalle saved = new Detalle();
        saved.setIdDetalle(5L);
        saved.setCantidad(2);
        saved.setSubTotal(20.0);
        saved.setProductoId(3L);
        saved.setIdVenta(venta);

        when(detalleRe.save(any(Detalle.class))).thenReturn(saved);

        DetalleRequestDTO dto = new DetalleRequestDTO(2, 1L, 3L);
        DetalleResponseDTO res = detalleService.crearDetalle(dto);

        assertEquals(5L, res.getIdDetalle());
        verify(ventaRe, times(1)).save(any(Venta.class));
    }

    @Test
    void crearDetalle_whenProductoMissing_shouldThrow() {
        when(productoClient.obtenerPorId(99L)).thenReturn(null);

        DetalleRequestDTO dto = new DetalleRequestDTO(1, 1L, 99L);
        assertThrows(ResourceNotFoundException.class, () -> detalleService.crearDetalle(dto));
    }
}
