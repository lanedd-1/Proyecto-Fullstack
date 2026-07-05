package com.semestral.inventario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.semestral.inventario.client.ProductosClient;
import com.semestral.inventario.dto.DescontarProductoRequestDTO;
import com.semestral.inventario.dto.EstanteRequestDTO;
import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.dto.PasilloRequestDTO;
import com.semestral.inventario.dto.ProductoDTO;
import com.semestral.inventario.dto.UbicacionRequestDTO;
import com.semestral.inventario.exception.ResourceNotFoundException;
import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Inventario;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;

import feign.FeignException;
import feign.Request;
import com.semestral.inventario.repository.EstanteRepository;
import com.semestral.inventario.repository.InventarioStockRepository;
import com.semestral.inventario.repository.PasilloRepository;
import com.semestral.inventario.repository.UbicacionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test unitario de InventarioService")
class InventarioServiceTest {

    @Mock
    private InventarioStockRepository inventarioRe;

    @Mock
    private UbicacionRepository ubicacionRe;

    @Mock
    private PasilloRepository pasillRe;

    @Mock
    private EstanteRepository estanteRe;

    @Mock
    private ProductosClient prodCli;

    @InjectMocks
    private InventarioService inventarioService;

    private Pasillo pasillo;
    private Estante estante;
    private Ubicacion ubicacion;
    private ProductoDTO productoDTO;
    private InventarioRequestDTO inventarioRequest;

    @BeforeEach
    void setUp() {
        pasillo = new Pasillo(10L, "Pasillo A");
        estante = new Estante(20L, "Estante 1");
        ubicacion = new Ubicacion(30L, pasillo, estante);
        productoDTO = new ProductoDTO(1L, "SKU-001", "Producto prueba", 199.99);

        inventarioRequest = new InventarioRequestDTO(1L, 10L, 20L, 5);
    }

    @Test
    @DisplayName("agregarStock() crea inventario nuevo cuando no existe en la ubicación")
    void agregarStock_debeCrearInventarioNuevo() {
        when(pasillRe.findById(10L)).thenReturn(Optional.of(pasillo));
        when(estanteRe.findById(20L)).thenReturn(Optional.of(estante));
        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.empty());
        when(ubicacionRe.save(any(Ubicacion.class))).thenReturn(ubicacion);
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventarioResponseDTO resultado = inventarioService.agregarStock(inventarioRequest);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProd());
        assertEquals("SKU-001", resultado.getSkuProducto());
        assertEquals("Producto prueba", resultado.getNombreProducto());
        assertEquals(5, resultado.getStock());
        assertEquals("Pasillo A", resultado.getNombrePasillo());
        assertEquals("Estante 1", resultado.getNombreEstante());

        verify(pasillRe, times(1)).findById(10L);
        verify(estanteRe, times(1)).findById(20L);
        verify(ubicacionRe, times(1)).findByPasilloAndEstante(10L, 20L);
        verify(inventarioRe, times(1)).save(any(Inventario.class));
    }

    @Test
    @DisplayName("agregarStock() suma stock cuando el inventario ya existe")
    void agregarStock_debeSumarStockExistente() {
        Inventario inventarioExistente = new Inventario(50L, 10, 1L, ubicacion);
        when(pasillRe.findById(10L)).thenReturn(Optional.of(pasillo));
        when(estanteRe.findById(20L)).thenReturn(Optional.of(estante));
        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.of(ubicacion));
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findByProductoYUbicacion(1L, 30L)).thenReturn(Optional.of(inventarioExistente));
        when(inventarioRe.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventarioResponseDTO resultado = inventarioService.agregarStock(inventarioRequest);

        assertNotNull(resultado);
        assertEquals(15, resultado.getStock());
        assertEquals(50L, resultado.getIdPps());

        verify(inventarioRe, times(1)).findByProductoYUbicacion(1L, 30L);
        verify(inventarioRe, times(1)).save(any(Inventario.class));
    }

    @Test
    @DisplayName("descontarStock() resta stock existente en la ubicación")
    void descontarStock_debeRestarStockExistente() {
        Inventario inventarioExistente = new Inventario(60L, 10, 1L, ubicacion);
        InventarioRequestDTO request = new InventarioRequestDTO(1L, 10L, 20L, 3);

        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.of(ubicacion));
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findByProductoYUbicacion(1L, 30L)).thenReturn(Optional.of(inventarioExistente));
        when(inventarioRe.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventarioResponseDTO resultado = inventarioService.descontarStock(request);

        assertNotNull(resultado);
        assertEquals(7, resultado.getStock());
        assertEquals(60L, resultado.getIdPps());

        verify(inventarioRe, times(1)).save(any(Inventario.class));
    }

    @Test
    @DisplayName("getStockPorProducto() devuelve stock cuando el producto existe")
    void getStockPorProducto_debeRetornarListaConProducto() {
        Inventario inventarioExistente = new Inventario(70L, 1, 1L, ubicacion);
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findByIdProd(1L)).thenReturn(List.of(inventarioExistente));

        List<InventarioResponseDTO> resultado = inventarioService.getStockPorProducto(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("SKU-001", resultado.get(0).getSkuProducto());
        assertEquals(1L, resultado.get(0).getIdProd());
    }

    @Test
    @DisplayName("getTodoStock() devuelve todos los registros de inventario")
    void getTodoStock_debeRetornarTodosLosInventarios() {
        Inventario inventarioExistente = new Inventario(80L, 1, 1L, ubicacion);
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findAll()).thenReturn(List.of(inventarioExistente));

        List<InventarioResponseDTO> resultado = inventarioService.getTodoStock();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Producto prueba", resultado.get(0).getNombreProducto());
    }

    @Test
    @DisplayName("crearPasillo() guarda y devuelve el pasillo")
    void crearPasillo_debeGuardarPasillo() {
        Pasillo pasilloGuardado = new Pasillo(11L, "Pasillo B");
        when(pasillRe.save(any(Pasillo.class))).thenReturn(pasilloGuardado);

        Pasillo resultado = inventarioService.crearPasillo(new PasilloRequestDTO("Pasillo B"));

        assertNotNull(resultado);
        assertEquals("Pasillo B", resultado.getNombrePasillo());
        verify(pasillRe, times(1)).save(any(Pasillo.class));
    }

    @Test
    @DisplayName("crearEstante() guarda y devuelve el estante")
    void crearEstante_debeGuardarEstante() {
        Estante estanteGuardado = new Estante(21L, "Estante 2");
        when(estanteRe.save(any(Estante.class))).thenReturn(estanteGuardado);

        Estante resultado = inventarioService.crearEstante(new EstanteRequestDTO("Estante 2"));

        assertNotNull(resultado);
        assertEquals("Estante 2", resultado.getNombreEstante());
        verify(estanteRe, times(1)).save(any(Estante.class));
    }

    @Test
    @DisplayName("crearUbicacion() reutiliza una ubicación existente")
    void crearUbicacion_debeReutilizarUbicacionExistente() {
        when(pasillRe.findById(10L)).thenReturn(Optional.of(pasillo));
        when(estanteRe.findById(20L)).thenReturn(Optional.of(estante));
        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.of(ubicacion));

        Ubicacion resultado = inventarioService.crearUbicacion(new UbicacionRequestDTO(10L, 20L));

        assertNotNull(resultado);
        assertEquals(30L, resultado.getIdPasEst());
        verify(ubicacionRe, times(0)).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("crearUbicacion() guarda nueva ubicación cuando no existe")
    void crearUbicacion_debeCrearNuevaUbicacion() {
        Ubicacion ubicacionNueva = new Ubicacion(31L, pasillo, estante);
        when(pasillRe.findById(10L)).thenReturn(Optional.of(pasillo));
        when(estanteRe.findById(20L)).thenReturn(Optional.of(estante));
        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.empty());
        when(ubicacionRe.save(any(Ubicacion.class))).thenReturn(ubicacionNueva);

        Ubicacion resultado = inventarioService.crearUbicacion(new UbicacionRequestDTO(10L, 20L));

        assertNotNull(resultado);
        assertEquals(31L, resultado.getIdPasEst());
        assertEquals("Pasillo A", resultado.getIdPasillo().getNombrePasillo());
        verify(ubicacionRe, times(1)).save(any(Ubicacion.class));
    }


    @Test
    @DisplayName("descontarStock() lanza NoSuchElementException cuando la ubicación no existe")
    void descontarStock_debeLanzarNoSuchElementCuandoUbicacionNoExiste() {
        InventarioRequestDTO request = new InventarioRequestDTO(1L, 10L, 20L, 2);
        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.empty());

        assertThrows(
            NoSuchElementException.class,
            () -> inventarioService.descontarStock(request)
        );
    }

    @Test
    @DisplayName("descontarStockPorProducto() lanza excepción cuando no hay stock suficiente")
    void descontarStockPorProducto_debeLanzarExcepcionCuandoNoHayStockDisponible() {
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findByIdProd(1L)).thenReturn(List.of(new Inventario(60L, 2, 1L, ubicacion)));

        assertThrows(
            IllegalArgumentException.class,
            () -> inventarioService.descontarStockPorProducto(new DescontarProductoRequestDTO(1L, 5))
        );
    }


    @Test
    @DisplayName("getTodoStock() devuelve N/A cuando el servicio de productos falla")
    void getTodoStock_debeDevolverProductoNADisponibleCuandoServicioFalla() {
        Inventario inventarioExistente = new Inventario(80L, 1, 1L, ubicacion);
        when(prodCli.obtenerProducto(1L)).thenThrow(new RuntimeException("error"));
        when(inventarioRe.findAll()).thenReturn(List.of(inventarioExistente));

        List<InventarioResponseDTO> resultado = inventarioService.getTodoStock();

        assertNotNull(resultado);
        assertEquals("N/A", resultado.get(0).getSkuProducto());
        assertEquals("Producto no disponible", resultado.get(0).getNombreProducto());
    }

    @Test
    @DisplayName("descontarStock() lanza excepción cuando no hay stock suficiente")
    void descontarStock_debeLanzarExcepcionCuandoNoHayStockSuficiente() {
        Inventario inventarioExistente = new Inventario(60L, 2, 1L, ubicacion);
        InventarioRequestDTO request = new InventarioRequestDTO(1L, 10L, 20L, 5);

        when(ubicacionRe.findByPasilloAndEstante(10L, 20L)).thenReturn(Optional.of(ubicacion));
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findByProductoYUbicacion(1L, 30L)).thenReturn(Optional.of(inventarioExistente));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> inventarioService.descontarStock(request)
        );

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
    }

    @Test
    @DisplayName("descontarStockPorProducto() descuenta stock cuando existe suficiente")
    void descontarStockPorProducto_debeDescontarCuandoHayStock() {
        Inventario inventarioExistente = new Inventario(60L, 10, 1L, ubicacion);
        when(prodCli.obtenerProducto(1L)).thenReturn(productoDTO);
        when(inventarioRe.findByIdProd(1L)).thenReturn(List.of(inventarioExistente));
        when(inventarioRe.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventarioResponseDTO resultado = inventarioService.descontarStockPorProducto(
            new DescontarProductoRequestDTO(1L, 3)
        );

        assertNotNull(resultado);
        assertEquals(7, resultado.getStock());
        verify(inventarioRe, times(1)).save(any(Inventario.class));
    }
}

