package com.semestral.productos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semestral.productos.dto.CategoriaRequestDTO;
import com.semestral.productos.dto.CategoriaResponseDTO;
import com.semestral.productos.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
@DisplayName("Tests del CategoriaController con MockMvc")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/categoria debe retornar 200 con lista de categorias")
    void listar_debeRetornar200ConListaDeCategorias() throws Exception {
        CategoriaResponseDTO dto = new CategoriaResponseDTO(1L, "Collares");

        when(categoriaService.findAllCat()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categoria")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idCat").value(1))
            .andExpect(jsonPath("$[0].nombreCat").value("Collares"));
    }

    @Test
    @DisplayName("POST /api/categoria/agregarCat debe retornar 201 con categoria creada")
    void crear_debeRetornar201CuandoDatosValidos() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Anillos");
        CategoriaResponseDTO response = new CategoriaResponseDTO(1L, "Collares");

        when(categoriaService.saveCat(any(CategoriaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/categoria/agregarCat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idCat").value(1))
            .andExpect(jsonPath("$.nombreCat").value("Collares"));
    }
}
