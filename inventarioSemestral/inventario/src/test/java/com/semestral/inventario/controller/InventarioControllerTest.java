package com.semestral.inventario.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semestral.inventario.dto.EstanteRequestDTO;
import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.dto.PasilloRequestDTO;
import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.service.InventarioService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = InventarioController.class)
@DisplayName("Test unitario de InventarioController")
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService invService;

    private InventarioResponseDTO inventarioResponse;
    private InventarioRequestDTO inventarioRequest;
    private PasilloRequestDTO pasilloRequest;
    private EstanteRequestDTO estanteRequest;
    private Pasillo pasillo;
    private Estante estante;

    @BeforeEach
    void setUp() {
        inventarioResponse = new InventarioResponseDTO(
            1L,
            1L,
            "SKU-001",
            "Producto prueba",
            150.0,
            "Pasillo A",
            "Estante 1",
            10
        );

        inventarioRequest = new InventarioRequestDTO(1L, 10L, 20L, 5);
        pasilloRequest = new PasilloRequestDTO("Pasillo A");
        estanteRequest = new EstanteRequestDTO("Estante 1");
        pasillo = new Pasillo(10L, "Pasillo A");
        estante = new Estante(20L, "Estante 1");
    }

    @Test
    @DisplayName("GET /api/inventario devuelve todo el stock")
    void obtenerTodoElStock_debeDevolverLista() throws Exception {
        when(invService.getTodoStock()).thenReturn(List.of(inventarioResponse));

        mockMvc.perform(get("/api/inventario").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].skuProducto").value("SKU-001"))
            .andExpect(jsonPath("$[0].nombreProducto").value("Producto prueba"))
            .andExpect(jsonPath("$[0].stock").value(10));

        verify(invService).getTodoStock();
    }

    @Test
    @DisplayName("GET /api/inventario/{id} devuelve stock de producto")
    void obtenerStockPorProducto_debeDevolverLista() throws Exception {
        when(invService.getStockPorProducto(1L)).thenReturn(List.of(inventarioResponse));

        mockMvc.perform(get("/api/inventario/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].skuProducto").value("SKU-001"))
            .andExpect(jsonPath("$[0].idProd").value(1));

        verify(invService).getStockPorProducto(1L);
    }

    @Test
    @DisplayName("PUT /api/inventario/agregar agrega stock")
    void agregarStock_debeLlamarServicio() throws Exception {
        when(invService.agregarStock(inventarioRequest)).thenReturn(inventarioResponse);

        mockMvc.perform(put("/api/inventario/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.skuProducto").value("SKU-001"))
            .andExpect(jsonPath("$.stock").value(10));

        verify(invService).agregarStock(inventarioRequest);
    }

    @Test
    @DisplayName("PUT /api/inventario/descontar descuenta stock")
    void descontarStock_debeLlamarServicio() throws Exception {
        when(invService.descontarStock(inventarioRequest)).thenReturn(inventarioResponse);

        mockMvc.perform(put("/api/inventario/descontar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.skuProducto").value("SKU-001"));

        verify(invService).descontarStock(inventarioRequest);
    }

    @Test
    @DisplayName("POST /api/inventario/pasillos crea un nuevo pasillo")
    void crearPasillo_debeRetornarPasillo() throws Exception {
        when(invService.crearPasillo(pasilloRequest)).thenReturn(pasillo);

        mockMvc.perform(post("/api/inventario/pasillos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pasilloRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idPasillo").value(10))
            .andExpect(jsonPath("$.nombrePasillo").value("Pasillo A"));

        verify(invService).crearPasillo(pasilloRequest);
    }

    @Test
    @DisplayName("POST /api/inventario/estantes crea un nuevo estante")
    void crearEstante_debeRetornarEstante() throws Exception {
        when(invService.crearEstante(estanteRequest)).thenReturn(estante);

        mockMvc.perform(post("/api/inventario/estantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estanteRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idEstante").value(20))
            .andExpect(jsonPath("$.nombreEstante").value("Estante 1"));

        verify(invService).crearEstante(estanteRequest);
    }
}

