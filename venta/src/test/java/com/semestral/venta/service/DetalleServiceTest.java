package com.semestral.venta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.semestral.venta.client.InventarioClient;
import com.semestral.venta.client.ProductoClient;
import com.semestral.venta.dto.DetalleRequestDTO;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.exception.ExternalServiceException;
import com.semestral.venta.exception.ResourceNotFoundException;
import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.DetalleRepository;
import com.semestral.venta.repository.VentaRepository;

import feign.FeignException;

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

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private DetalleService detalleService;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setIdVenta(1L);
        venta.setFechaV(LocalDateTime.now());
        venta.setTotal(0.0);
        venta.setDetalles(new ArrayList<>());

        lenient().when(ventaRe.findById(1L)).thenReturn(Optional.of(venta));
    }

    @Test
    void obtenerTodos_shouldReturnListOfDetalles() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        when(detalleRe.findAll()).thenReturn(java.util.List.of(d));

        var result = detalleService.obtenerTodos();
        assertEquals(1, result.size());
        verify(detalleRe, times(1)).findAll();
    }

    @Test
    void obtenerTodos_shouldReturnEmptyList() {
        when(detalleRe.findAll()).thenReturn(new ArrayList<>());

        var result = detalleService.obtenerTodos();
        assertEquals(0, result.size());
    }

    @Test
    void obtenerPorId_shouldReturnDetalle_whenExists() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        when(detalleRe.findById(1L)).thenReturn(Optional.of(d));

        DetalleResponseDTO result = detalleService.obtenerPorId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdDetalle());
    }

    @Test
    void obtenerPorId_shouldThrowException_whenNotExists() {
        when(detalleRe.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> detalleService.obtenerPorId(999L));
    }

    @Test
    void crearDetalle_happyPath_updatesVentaTotalAndReturnsDTO() {
        Map<String, Object> producto = new HashMap<>();
        producto.put("idProd", 3L);
        producto.put("precioProd", 10.0);
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        when(detalleRe.findByIdVenta_IdVentaAndProductoId(eq(1L), eq(3L))).thenReturn(Optional.empty());
        when(inventarioClient.descontarStockPorProducto(any())).thenReturn(new HashMap<>());

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
        verify(inventarioClient, times(1)).descontarStockPorProducto(any());
        verify(ventaRe, times(1)).save(any(Venta.class));
    }

    @Test
    void crearDetalle_shouldThrowException_withNullVentaId() {
        DetalleRequestDTO dto = new DetalleRequestDTO(1, null, 3L);
        assertThrows(IllegalArgumentException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void crearDetalle_shouldThrowException_withNullProductoId() {
        DetalleRequestDTO dto = new DetalleRequestDTO(1, 1L, null);
        assertThrows(IllegalArgumentException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void crearDetalle_shouldThrowException_withNullCantidad() {
        DetalleRequestDTO dto = new DetalleRequestDTO(null, 1L, 3L);
        assertThrows(IllegalArgumentException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void crearDetalle_shouldThrowException_withZeroCantidad() {
        DetalleRequestDTO dto = new DetalleRequestDTO(0, 1L, 3L);
        assertThrows(IllegalArgumentException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void crearDetalle_shouldThrowException_withNegativeCantidad() {
        DetalleRequestDTO dto = new DetalleRequestDTO(-5, 1L, 3L);
        assertThrows(IllegalArgumentException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void crearDetalle_shouldThrowException_whenVentaNotFound() {
        when(ventaRe.findById(99L)).thenReturn(Optional.empty());
        DetalleRequestDTO dto = new DetalleRequestDTO(1, 99L, 3L);
        assertThrows(ResourceNotFoundException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void crearDetalle_whenProductoNotFound_shouldThrow() {
        when(productoClient.obtenerPorId(99L)).thenReturn(null);
        DetalleRequestDTO dto = new DetalleRequestDTO(1, 1L, 99L);
        assertThrows(ResourceNotFoundException.class, () -> detalleService.crearDetalle(dto));
    }


    @Test
    void crearDetalle_shouldThrowException_whenProductoIdNotInMap() {
        Map<String, Object> emptyMap = new HashMap<>();
        when(productoClient.obtenerPorId(3L)).thenReturn(emptyMap);

        DetalleRequestDTO dto = new DetalleRequestDTO(2, 1L, 3L);
        assertThrows(ResourceNotFoundException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void actualizarDetalle_shouldReturnUpdatedDetalle() {
        Detalle existing = new Detalle();
        existing.setIdDetalle(1L);
        existing.setIdVenta(venta);
        existing.setProductoId(3L);
        existing.setCantidad(2);
        existing.setSubTotal(20.0);

        when(detalleRe.findById(1L)).thenReturn(Optional.of(existing));

        Map<String, Object> producto = new HashMap<>();
        producto.put("idProd", 3L);
        producto.put("precioProd", 10.0);
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        when(detalleRe.save(any(Detalle.class))).thenReturn(existing);

        DetalleRequestDTO dto = new DetalleRequestDTO(3, 1L, 3L);
        DetalleResponseDTO result = detalleService.actualizarDetalle(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getIdDetalle());
        verify(detalleRe).save(any(Detalle.class));
    }

    @Test
    void actualizarDetalle_shouldThrowException_whenDetalleNotFound() {
        when(detalleRe.findById(999L)).thenReturn(Optional.empty());
        DetalleRequestDTO dto = new DetalleRequestDTO(1, 1L, 3L);
        assertThrows(ResourceNotFoundException.class, 
            () -> detalleService.actualizarDetalle(999L, dto));
    }

    @Test
    void actualizarDetalle_shouldThrowException_whenVentaNotFound() {
        Detalle existing = new Detalle();
        existing.setIdDetalle(1L);
        when(detalleRe.findById(1L)).thenReturn(Optional.of(existing));
        lenient().when(ventaRe.findById(99L)).thenReturn(Optional.empty());

        DetalleRequestDTO dto = new DetalleRequestDTO(1, 99L, 3L);
        assertThrows(ResourceNotFoundException.class, 
            () -> detalleService.actualizarDetalle(1L, dto));
    }

    @Test
    void eliminarDetalle_shouldDeleteDetalle_whenExists() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        when(detalleRe.findById(1L)).thenReturn(Optional.of(d));

        detalleService.eliminarDetalle(1L);

        verify(detalleRe).deleteById(1L);
    }

    @Test
    void eliminarDetalle_shouldThrowException_whenNotExists() {
        when(detalleRe.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> detalleService.eliminarDetalle(999L));
    }

    @Test
    void convertToDTO_shouldReturnValidDTO() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        d.setCantidad(2);
        d.setSubTotal(20.0);
        d.setProductoId(3L);

        DetalleResponseDTO dto = detalleService.convertToDTO(d);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdDetalle());
        assertEquals(2, dto.getCantidad());
        assertEquals(20.0, dto.getSubTotal());
    }

    @Test
    void crearDetalle_shouldUpdateExistingDetalle_whenAlreadyExists() {
        Map<String, Object> producto = new HashMap<>();
        producto.put("idProd", 3L);
        producto.put("precioProd", 10.0);
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        Detalle existingDetalle = new Detalle();
        existingDetalle.setIdDetalle(5L);
        existingDetalle.setCantidad(2);
        existingDetalle.setSubTotal(20.0);
        existingDetalle.setProductoId(3L);
        existingDetalle.setIdVenta(venta);

        when(detalleRe.findByIdVenta_IdVentaAndProductoId(eq(1L), eq(3L))).thenReturn(Optional.of(existingDetalle));
        when(inventarioClient.descontarStockPorProducto(any())).thenReturn(new HashMap<>());
        when(detalleRe.save(any(Detalle.class))).thenReturn(existingDetalle);

        DetalleRequestDTO dto = new DetalleRequestDTO(3, 1L, 3L);
        DetalleResponseDTO res = detalleService.crearDetalle(dto);

        assertEquals(5L, res.getIdDetalle());
        verify(detalleRe, times(1)).save(any(Detalle.class));
        verify(ventaRe, times(1)).save(any(Venta.class));
    }

    @Test
    void convertToDTO_shouldMapProductName_whenProductoIdExists() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        d.setCantidad(2);
        d.setSubTotal(20.0);
        d.setProductoId(3L);

        Map<String, Object> producto = new HashMap<>();
        producto.put("nombreProd", "Producto Test");
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        DetalleResponseDTO dto = detalleService.convertToDTO(d);

        assertNotNull(dto);
        assertEquals("Producto Test", dto.getProductoNombre());
    }

    @Test
    void actualizarDetalle_shouldChangeProducto_whenProductIdChanges() {
        Detalle existing = new Detalle();
        existing.setIdDetalle(1L);
        existing.setIdVenta(venta);
        existing.setProductoId(3L);
        existing.setCantidad(2);
        existing.setSubTotal(20.0);

        when(detalleRe.findById(1L)).thenReturn(Optional.of(existing));

        Map<String, Object> nuevoProducto = new HashMap<>();
        nuevoProducto.put("idProd", 5L);
        nuevoProducto.put("precioProd", 15.0);
        when(productoClient.obtenerPorId(5L)).thenReturn(nuevoProducto);

        when(detalleRe.save(any(Detalle.class))).thenReturn(existing);

        DetalleRequestDTO dto = new DetalleRequestDTO(2, 1L, 5L);
        DetalleResponseDTO result = detalleService.actualizarDetalle(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getIdDetalle());
        verify(detalleRe).save(any(Detalle.class));
    }

    @Test
    void actualizarDetalle_shouldMoveDetalleToNewVenta() {
        Detalle existing = new Detalle();
        existing.setIdDetalle(1L);
        Venta ventaOriginal = new Venta();
        ventaOriginal.setIdVenta(1L);
        ventaOriginal.setTotal(20.0);
        existing.setIdVenta(ventaOriginal);
        existing.setProductoId(3L);
        existing.setCantidad(2);
        existing.setSubTotal(20.0);

        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdVenta(2L);
        nuevaVenta.setTotal(0.0);

        when(detalleRe.findById(1L)).thenReturn(Optional.of(existing));
        when(ventaRe.findById(2L)).thenReturn(Optional.of(nuevaVenta));

        Map<String, Object> producto = new HashMap<>();
        producto.put("idProd", 3L);
        producto.put("precioProd", 10.0);
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        when(detalleRe.save(any(Detalle.class))).thenReturn(existing);

        DetalleRequestDTO dto = new DetalleRequestDTO(2, 2L, 3L);
        DetalleResponseDTO result = detalleService.actualizarDetalle(1L, dto);

        assertNotNull(result);
        verify(ventaRe, times(2)).save(any(Venta.class));
    }

    @Test
    void crearDetalle_shouldThrowException_whenInventarioFails() {
        Map<String, Object> producto = new HashMap<>();
        producto.put("idProd", 3L);
        producto.put("precioProd", 10.0);
        when(productoClient.obtenerPorId(3L)).thenReturn(producto);

        lenient().when(detalleRe.findByIdVenta_IdVentaAndProductoId(eq(1L), eq(3L))).thenReturn(Optional.empty());
        
        doThrow(IllegalArgumentException.class).when(inventarioClient).descontarStockPorProducto(any());

        DetalleRequestDTO dto = new DetalleRequestDTO(10, 1L, 3L);
        assertThrows(IllegalArgumentException.class, () -> detalleService.crearDetalle(dto));
    }

    @Test
    void eliminarDetalle_shouldUpdateVentaTotal() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        d.setSubTotal(50.0);
        Venta v = new Venta();
        v.setIdVenta(1L);
        v.setTotal(100.0);
        d.setIdVenta(v);

        when(detalleRe.findById(1L)).thenReturn(Optional.of(d));

        detalleService.eliminarDetalle(1L);

        verify(ventaRe).save(any(Venta.class));
        verify(detalleRe).deleteById(1L);
    }

    @Test
    void convertToDTO_shouldHandleNullProductoId() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        d.setCantidad(2);
        d.setSubTotal(20.0);
        d.setProductoId(null);

        DetalleResponseDTO dto = detalleService.convertToDTO(d);

        assertNotNull(dto);
        assertNull(dto.getProductoNombre());
    }

    @Test
    void convertToDTO_shouldHandleExceptionInProductoClient() {
        Detalle d = new Detalle();
        d.setIdDetalle(1L);
        d.setCantidad(2);
        d.setSubTotal(20.0);
        d.setProductoId(3L);

        when(productoClient.obtenerPorId(3L))
            .thenThrow(new RuntimeException("Service error"));

        DetalleResponseDTO dto = detalleService.convertToDTO(d);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdDetalle());
    }
}


