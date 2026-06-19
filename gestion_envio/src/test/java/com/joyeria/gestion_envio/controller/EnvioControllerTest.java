package com.joyeria.gestion_envio.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.joyeria.gestion_envio.dto.EnvioRequestDTO;
import com.joyeria.gestion_envio.dto.EnvioResponseDTO;
import com.joyeria.gestion_envio.service.EnvioService;

@WebMvcTest(EnvioController.class)
@DisplayName("Tests del EnvioController con MockMvc")
public class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService envioService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("GET api/envios debe retornar un JSON con la lista de envios y codigo 200")
    void obtenerTodos_debeRetornar200ConListaDeEnvios() throws Exception {
        EnvioResponseDTO dto = new EnvioResponseDTO(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(3), 10L, 5L, "PREPARACION");
        
        when(envioService.getAllEnvios()).thenReturn(List.of(dto));
        
        mockMvc.perform(get("/api/envios")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEnvio").value(1))
                .andExpect(jsonPath("$[0].estado").value("PREPARACION"));
    }

    @Test
    @DisplayName("GET api/envios/{id} debe retornar 200 y el envio cuando existe")
    void obtenerPorId_debeRetornar200_cuandoEnvioExiste() throws Exception {
        EnvioResponseDTO dto = new EnvioResponseDTO(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(3), 10L, 5L, "PREPARACION");
        
        when(envioService.findByIdOrThrow(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/envios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(10))
                .andExpect(jsonPath("$.idDireccion").value(5));
    }

    @Test
    @DisplayName("POST api/envios debe retornar 201 con datos validos")
    void crearEnvio_debeRetornar201_cuandoDatosValidos() throws Exception {
        EnvioRequestDTO request = new EnvioRequestDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(3), 10L, 5L, "PREPARACION");
        EnvioResponseDTO response = new EnvioResponseDTO(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(3), 10L, 5L, "PREPARACION");
        
        when(envioService.saveEnvio(any(EnvioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("PREPARACION"));
    }

    @Test
    @DisplayName("PUT api/envios/{id} debe retornar 200 al actualizar correctamente")
    void actualizarEnvio_debeRetornar200_cuandoDatosValidos() throws Exception {
        EnvioRequestDTO request = new EnvioRequestDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(3), 10L, 5L, "EN_CAMINO");
        EnvioResponseDTO response = new EnvioResponseDTO(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(3), 10L, 5L, "EN_CAMINO");
        
        when(envioService.update(eq(1L), any(EnvioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/envios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_CAMINO"));
    }
}