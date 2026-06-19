package com.joyeria.gestion_envio.controller;

import java.time.LocalDateTime;
import java.util.List;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.joyeria.gestion_envio.dto.HistorialRequestDTO;
import com.joyeria.gestion_envio.dto.HistorialResponseDTO;
import com.joyeria.gestion_envio.service.HistorialService;

@WebMvcTest(HistorialController.class)
@DisplayName("Tests del HistorialController con MockMvc")
public class HistorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistorialService historialService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("GET api/historial debe retornar JSON con historial y codigo 200")
    void obtenerTodoElHistorial_debeRetornar200() throws Exception {
        HistorialResponseDTO dto = new HistorialResponseDTO(1L, 10L, LocalDateTime.now(), "ENTREGADO", 5L);
        
        when(historialService.getAllHistorial()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/historial")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idHistorial").value(1))
                .andExpect(jsonPath("$[0].estado").value("ENTREGADO"));
    }

    @Test
    @DisplayName("GET api/historial/envio/{envioId} debe retornar historial filtrado por envio")
    void obtenerHistorialPorEnvio_debeRetornar200() throws Exception {
        HistorialResponseDTO dto = new HistorialResponseDTO(1L, 10L, LocalDateTime.now(), "PREPARACION", 5L);
        
        when(historialService.getHistorialByEnvioId(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/historial/envio/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEnvio").value(10))
                .andExpect(jsonPath("$[0].estado").value("PREPARACION"));
    }
    @Test
    @DisplayName("POST api/historial debe retornar 201 al registrar nuevo movimiento")
    void crearHistorial_debeRetornar201_cuandoDatosValidos() throws Exception {
        HistorialRequestDTO request = new HistorialRequestDTO(10L, "ENTREGADO", 5L);
        HistorialResponseDTO response = new HistorialResponseDTO(1L, 10L, LocalDateTime.now(), "ENTREGADO", 5L);
        
        when(historialService.saveHistorial(any(HistorialRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/historial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idHistorial").value(1))
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }
}