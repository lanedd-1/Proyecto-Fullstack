package com.semestral.gestion_direccion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.exception.ResourceNotFoundException;
import com.semestral.gestion_direccion.service.DireccionService;

@WebMvcTest(DireccionController.class)
@DisplayName("Test de Integración del Controlador (DireccionController)")
public class DireccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DireccionService direccionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DireccionRequestDTO requestDTO;
    private DireccionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new DireccionRequestDTO();
        requestDTO.setCalle("Av. Siempre Viva");
        requestDTO.setNumero("742");
        requestDTO.setIdComuna(1L);
        requestDTO.setIdUsuario(10L);
        requestDTO.setIdEstado(1L);

        responseDTO = new DireccionResponseDTO(1L, "Av. Siempre Viva", "742", "Quilicura", "Metropolitana",1L,1L);
    }

    @Test
    @DisplayName("GET /api/direcciones - Debe retornar 200 OK y la lista")
    void getAll_debeRetornar200() throws Exception {
        when(direccionService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/direcciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calle").value("Av. Siempre Viva"));
    }

    @Test
    @DisplayName("GET /api/direcciones/{id} - Debe retornar 200 cuando existe")
    void getById_debeRetornar200() throws Exception {
        when(direccionService.findByIdOrThrow(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/direcciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calle").value("Av. Siempre Viva"));
    }

    @Test
    @DisplayName("GET /api/direcciones/{id} - Debe retornar 404 cuando no existe")
    void getById_debeRetornar404() throws Exception {
        when(direccionService.findByIdOrThrow(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(get("/api/direcciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/direcciones - Debe retornar 201 Created")
    void create_debeRetornar201() throws Exception {
        when(direccionService.create(any(DireccionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/direcciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calle").value("Av. Siempre Viva"));
    }

    @Test
    @DisplayName("PUT /api/direcciones/{id} - Debe retornar 200 OK")
    void update_debeRetornar200() throws Exception {
        when(direccionService.update(eq(1L), any(DireccionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/direcciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/direcciones/{id} - Debe retornar 204 No Content")
    void delete_debeRetornar204() throws Exception {
        doNothing().when(direccionService).delete(1L);

        mockMvc.perform(delete("/api/direcciones/1"))
                .andExpect(status().isNoContent());
    }
}