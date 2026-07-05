package com.semestral.venta.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semestral.venta.Controller.Controller;
import com.semestral.venta.dto.DetalleRequestDTO;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.exception.ResourceNotFoundException;
import com.semestral.venta.service.DetalleService;
import com.semestral.venta.service.VentaService;

@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private DetalleService detalleService;

    private VentaResponseDTO ventaResponseDTO;
    private DetalleResponseDTO detalleResponseDTO;

    @BeforeEach
    void setUp() {
        ventaResponseDTO = new VentaResponseDTO();
        ventaResponseDTO.setIdVenta(1L);
        ventaResponseDTO.setFechaV("2024-06-15");
        ventaResponseDTO.setTotal(100.0);
        ventaResponseDTO.setDetalles(new ArrayList<>());

        detalleResponseDTO = new DetalleResponseDTO();
        detalleResponseDTO.setIdDetalle(1L);
        detalleResponseDTO.setVentaId(1L);
        detalleResponseDTO.setProductoId(1L);
        detalleResponseDTO.setCantidad(2);
        detalleResponseDTO.setSubTotal(50.0);
        detalleResponseDTO.setProductoNombre("Producto Test");
    }

    // ============ VENTA TESTS ============

    @Test
    void getAllVentas_shouldReturnList() throws Exception {
        when(ventaService.obtenerTodas()).thenReturn(List.of(ventaResponseDTO));

        mockMvc.perform(get("/api/ventas/verVentas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].idVenta").value(1));
    }

    @Test
    void getAllVentas_shouldReturnEmptyList() throws Exception {
        when(ventaService.obtenerTodas()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ventas/verVentas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getVenta_shouldReturn200_whenVentaExists() throws Exception {
        when(ventaService.obtenerPorId(1L)).thenReturn(ventaResponseDTO);

        mockMvc.perform(get("/api/ventas/ventas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idVenta").value(1))
            .andExpect(jsonPath("$.fechaV").value("2024-06-15"))
            .andExpect(jsonPath("$.total").value(100.0));
    }

    @Test
    void getVenta_shouldReturn404_whenVentaDoesNotExist() throws Exception {
        when(ventaService.obtenerPorId(999L))
            .thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(get("/api/ventas/ventas/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createVenta_shouldReturn201_withValidData() throws Exception {
        VentaRequestDTO request = new VentaRequestDTO();
        request.setFechaV("2024-06-15");

        when(ventaService.crearVenta(any(VentaRequestDTO.class)))
            .thenReturn(ventaResponseDTO);

        mockMvc.perform(post("/api/ventas/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idVenta").value(1));
    }

    @Test
    void createVenta_shouldReturn400_withBlankFecha() throws Exception {
        VentaRequestDTO request = new VentaRequestDTO();
        request.setFechaV("");

        mockMvc.perform(post("/api/ventas/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createVenta_shouldReturn400_withNullFecha() throws Exception {
        String requestJson = "{}";

        mockMvc.perform(post("/api/ventas/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    //DETALLE TEST

    @Test
    void getAllDetalles_shouldReturnList() throws Exception {
        when(detalleService.obtenerTodos()).thenReturn(List.of(detalleResponseDTO));

        mockMvc.perform(get("/api/ventas/verDetalles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].idDetalle").value(1));
    }

    @Test
    void getAllDetalles_shouldReturnEmptyList() throws Exception {
        when(detalleService.obtenerTodos()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ventas/verDetalles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDetalle_shouldReturn200_whenDetalleExists() throws Exception {
        when(detalleService.obtenerPorId(1L)).thenReturn(detalleResponseDTO);

        mockMvc.perform(get("/api/ventas/detalles/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idDetalle").value(1))
            .andExpect(jsonPath("$.cantidad").value(2))
            .andExpect(jsonPath("$.subTotal").value(50.0));
    }

    @Test
    void getDetalle_shouldReturn404_whenDetalleDoesNotExist() throws Exception {
        when(detalleService.obtenerPorId(999L))
            .thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(get("/api/ventas/detalles/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createDetalle_shouldReturn201_withValidData() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setVentaId(1L);
        request.setProductoId(1L);
        request.setCantidad(2);

        when(detalleService.crearDetalle(any(DetalleRequestDTO.class)))
            .thenReturn(detalleResponseDTO);

        mockMvc.perform(post("/api/ventas/detalles/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idDetalle").value(1));
    }

    @Test
    void createDetalle_shouldReturn400_withNullVentaId() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setProductoId(1L);
        request.setCantidad(2);

        mockMvc.perform(post("/api/ventas/detalles/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createDetalle_shouldReturn400_withNullProductoId() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setVentaId(1L);
        request.setCantidad(2);

        mockMvc.perform(post("/api/ventas/detalles/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createDetalle_shouldReturn400_withNullCantidad() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setVentaId(1L);
        request.setProductoId(1L);

        mockMvc.perform(post("/api/ventas/detalles/crear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateDetalle_shouldReturn200_withValidData() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setVentaId(1L);
        request.setProductoId(1L);
        request.setCantidad(3);

        when(detalleService.actualizarDetalle(eq(1L), any(DetalleRequestDTO.class)))
            .thenReturn(detalleResponseDTO);

        mockMvc.perform(put("/api/ventas/detalles/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idDetalle").value(1));
    }

    @Test
    void updateDetalle_shouldReturn404_whenDetalleDoesNotExist() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setVentaId(1L);
        request.setProductoId(1L);
        request.setCantidad(3);

        when(detalleService.actualizarDetalle(eq(999L), any(DetalleRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(put("/api/ventas/detalles/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateDetalle_shouldReturn400_withNullVentaId() throws Exception {
        DetalleRequestDTO request = new DetalleRequestDTO();
        request.setProductoId(1L);
        request.setCantidad(3);

        mockMvc.perform(put("/api/ventas/detalles/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDetalle_shouldReturn204_whenDetalleExists() throws Exception {
        doNothing().when(detalleService).eliminarDetalle(1L);

        mockMvc.perform(delete("/api/ventas/detalles/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteDetalle_shouldReturn404_whenDetalleDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException(999L))
            .when(detalleService).eliminarDetalle(999L);

        mockMvc.perform(delete("/api/ventas/detalles/999"))
            .andExpect(status().isNotFound());
    }
}
