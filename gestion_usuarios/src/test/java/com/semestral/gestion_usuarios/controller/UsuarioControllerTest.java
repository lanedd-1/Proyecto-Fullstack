package com.semestral.gestion_usuarios.controller;

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

import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
@DisplayName("Tests del UsuarioController con MockMvc")
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET api/usuarios debe retornar un JSON con la lista de usuarios y el codigo 200")
    void listar_debeRetornar200ConListaDeUsuarios() throws Exception {
        // simular usuarios
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Juan Perez", "11111111-1", "juan@mail.com", 1L, "ADMIN", 1L);
        
        when(usuarioService.getAllUsuarios()).thenReturn(List.of(dto));
        
        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombreU").value("Juan Perez"))
                .andExpect(jsonPath("$[0].correoU").value("juan@mail.com"));
    }

    @Test
    @DisplayName("POST api/usuarios debe retorna 201 con datos validos")
    void crear_debeRetornar201_cuandoDatosValidos() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Juan Perez", "11111111-1", "juan@mail.com", "secreta123", 1L, 1L);
        UsuarioResponseDTO response = new UsuarioResponseDTO(1L, "Juan Perez", "11111111-1", "juan@mail.com", 1L, "ADMIN", 1L);
        
        when(usuarioService.saveUsuario(any(UsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombreU").value("Juan Perez"))
                .andExpect(jsonPath("$.correoU").value("juan@mail.com"));
    }
    @Test
    @DisplayName("PUT api/usuarios/{id} debe retornar 200 al actualizar correctamente")
    void actualizar_debeRetornar200_cuandoDatosValidos() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Juan Actualizado", "11111111-1", "juan_nuevo@mail.com", "", 1L, 1L);
        UsuarioResponseDTO response = new UsuarioResponseDTO(1L, "Juan Actualizado", "11111111-1", "juan_nuevo@mail.com", 1L, "ADMIN", 1L);
        
        when(usuarioService.update(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.nombreU").value("Juan Actualizado"))
                .andExpect(jsonPath("$.correoU").value("juan_nuevo@mail.com"));
    }
}