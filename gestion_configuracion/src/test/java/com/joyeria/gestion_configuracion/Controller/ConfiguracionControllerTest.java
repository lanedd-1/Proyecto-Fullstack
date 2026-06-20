package com.joyeria.gestion_configuracion.Controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyeria.gestion_configuracion.controller.ConfiguracionController;
import com.joyeria.gestion_configuracion.dto.ConfiguracionRequestDTO;
import com.joyeria.gestion_configuracion.dto.ConfiguracionResponseDTO;
import com.joyeria.gestion_configuracion.exception.ConfiguracionLongitudInvalidaException;
import com.joyeria.gestion_configuracion.exception.ConfiguracionNotFoundException;
import com.joyeria.gestion_configuracion.service.ConfiguracionService;

@WebMvcTest(ConfiguracionController.class)
@DisplayName("Tests del ConfiguracionController con MockMvc")
public class ConfiguracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfiguracionService configuracionService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("GET /api/configuracion debe retornar 200 con la configuracion existente")
    void get_debeRetornar200ConConfiguracion() throws Exception {
        ConfiguracionResponseDTO dto = new ConfiguracionResponseDTO(
                8, 20, true, true, true, true, "!@#$%&*", List.of());
        when(configuracionService.getConfiguracion()).thenReturn(dto);

        mockMvc.perform(get("/api/configuracion")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.longitudMinima").value(8))
                .andExpect(jsonPath("$.longitudMaxima").value(20));
    }

    @Test
    @DisplayName("GET /api/configuracion debe retornar 404 cuando no existe configuracion en la BD")
    void get_debeRetornar404_cuandoNoExisteConfiguracion() throws Exception {
        when(configuracionService.getConfiguracion()).thenThrow(new ConfiguracionNotFoundException());

        mockMvc.perform(get("/api/configuracion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PUT /api/configuracion debe retornar 200 con datos validos")
    void update_debeRetornar200_cuandoDatosValidos() throws Exception {
        ConfiguracionRequestDTO request = new ConfiguracionRequestDTO(
                8, 20, true, true, true, true, "!@#$%&*");
        ConfiguracionResponseDTO response = new ConfiguracionResponseDTO(
                8, 20, true, true, true, true, "!@#$%&*", List.of());
        when(configuracionService.update(any(ConfiguracionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/configuracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.longitudMinima").value(8));
    }

    @Test
    @DisplayName("PUT /api/configuracion debe retornar 400 cuando faltan campos obligatorios")
    void update_debeRetornar400_cuandoFaltanCampos() throws Exception {
        ConfiguracionRequestDTO request = new ConfiguracionRequestDTO(
                null, null, null, null, null, null, null);

        mockMvc.perform(put("/api/configuracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/configuracion debe retornar 400 cuando la longitud minima es invalida")
    void update_debeRetornar400_cuandoLongitudInvalida() throws Exception {
        ConfiguracionRequestDTO request = new ConfiguracionRequestDTO(
                20, 20, true, true, true, true, "!@#$%&*");
        when(configuracionService.update(any(ConfiguracionRequestDTO.class)))
                .thenThrow(new ConfiguracionLongitudInvalidaException(20, 20));

        mockMvc.perform(put("/api/configuracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
