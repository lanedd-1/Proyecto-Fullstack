package com.semestral.productos.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.dto.ProductoResponseDTO;
import com.semestral.productos.service.ProductoService;

@WebMvcTest(ProductoController.class)
@DisplayName("Tests del ProductoController con MockMvc")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/productos debe retornar 200 con lista de productos")
    void listar_debeRetornar200ConListaDeProductos() throws Exception {
        ProductoResponseDTO dto = new ProductoResponseDTO(
            1L,
            "80818902",
            "Collar de oro 9 kilates",
            "Descripción del collar",
            BigDecimal.valueOf(820.00),
            "https://example.com/collar.jpeg",
            null
        );

        when(productoService.getAllProductos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idProd").value(1))
            .andExpect(jsonPath("$[0].sku").value("80818902"))
            .andExpect(jsonPath("$[0].nombreProd").value("Collar de oro 9 kilates"));
    }

    @Test
    @DisplayName("POST /api/productos/agregar debe retornar 201 con datos validos")
    void crear_debeRetornar201CuandoDatosValidos() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO(
            "80818902",
            "Collar de oro 9 kilates",
            "Descripción del collar",
            BigDecimal.valueOf(820.00),
            "https://example.com/collar.jpeg",
            1L
        );

        ProductoResponseDTO response = new ProductoResponseDTO(
            1L,
            "80818902",
            "Collar de oro 9 kilates",
            "Descripción del collar",
            BigDecimal.valueOf(820.00),
            "https://example.com/collar.jpeg",
            null
        );

        when(productoService.saveProducto(any(ProductoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/productos/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idProd").value(1))
            .andExpect(jsonPath("$.nombreProd").value("Collar de oro 9 kilates"));
    }

    @Test
    @DisplayName("GET /api/productos/{id} debe retornar 200 cuando existe el producto")
    void obtenerPorId_debeRetornar200ConProducto() throws Exception {
        ProductoResponseDTO dto = new ProductoResponseDTO(
            1L,
            "80818902",
            "Collar de oro 9 kilates",
            "Descripción del collar",
            BigDecimal.valueOf(820.00),
            "https://example.com/collar.jpeg",
            null
        );

        when(productoService.encontrarPorId(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idProd").value(1))
            .andExpect(jsonPath("$.sku").value("80818902"))
            .andExpect(jsonPath("$.nombreProd").value("Collar de oro 9 kilates"));
    }

    @Test
    @DisplayName("GET /api/productos/{id} debe retornar 404 cuando no existe el producto")
    void obtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {
        when(productoService.encontrarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/999")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/productos/{id}/precio debe retornar 200 con precio actualizado")
    void actualizarPrecio_debeRetornar200ConPrecioActualizado() throws Exception {
        ProductoResponseDTO updated = new ProductoResponseDTO(
            1L,
            "80818902",
            "Collar de oro 9 kilates",
            "Descripción del collar",
            BigDecimal.valueOf(900.00),
            "https://example.com/collar.jpeg",
            null
        );

        when(productoService.updatePrecio(any(Long.class), any(BigDecimal.class))).thenReturn(updated);

        mockMvc.perform(put("/api/productos/1/precio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BigDecimal.valueOf(900.00))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.precioUnitario").value(900.00));
    }

    @Test
    @DisplayName("PUT /api/productos/{id}/descripcion debe retornar 200 con descripcion actualizada")
    void actualizarDescripcion_debeRetornar200ConDescripcionActualizada() throws Exception {
        ProductoResponseDTO updated = new ProductoResponseDTO(
            1L,
            "80818902",
            "Collar de oro 9 kilates",
            "Nueva descripción",
            BigDecimal.valueOf(820.00),
            "https://example.com/collar.jpeg",
            null
        );

        when(productoService.updateDescripcion(any(Long.class), any(String.class))).thenReturn(updated);

        // Enviar solo el campo descProd en el body
        String body = "{\"descProd\": \"Nueva descripción\"}";

        mockMvc.perform(put("/api/productos/1/descripcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descProd").value("Nueva descripción"));
    }
}
