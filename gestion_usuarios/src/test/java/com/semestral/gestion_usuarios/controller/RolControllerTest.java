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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.service.RolService;

@WebMvcTest(RolController.class)
@DisplayName("Tests del RolController con MockMvc")
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolService rolService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/roles debe retornar 200 con la lista de roles")
    void listar_debeRetornar200ConListaDeRoles() throws Exception {
        Rol rol = new Rol(1L, "ADMIN");
        when(rolService.findAll()).thenReturn(List.of(rol));

        mockMvc.perform(get("/api/roles").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreRol").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /api/roles/{id} debe retornar 200 cuando el rol existe")
    void getById_debeRetornar200_cuandoExiste() throws Exception {
        Rol rol = new Rol(1L, "ADMIN");
        when(rolService.getById(1L)).thenReturn(rol);

        mockMvc.perform(get("/api/roles/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/roles/guardar debe retornar 201 cuando el rol es creado")
    void crearRol_debeRetornar201_cuandoEsValido() throws Exception {
        Rol nuevo = new Rol(null, "SUPERVISOR");
        Rol creado = new Rol(3L, "SUPERVISOR");

        when(rolService.create(any(Rol.class))).thenReturn(creado);

        mockMvc.perform(post("/api/roles/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreRol").value("SUPERVISOR"));
    }

    @Test
    @DisplayName("POST /api/roles/guardar debe retornar 500 cuando el service lanza una excepcion")
    void crearRol_debeRetornar500_cuandoFalla() throws Exception {
        Rol nuevo = new Rol(null, "ADMIN");

        when(rolService.create(any(Rol.class)))
                .thenThrow(new RuntimeException("Rol ya existe: ADMIN"));

        mockMvc.perform(post("/api/roles/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/roles/{id} debe retornar 200 cuando la actualizacion es correcta")
    void update_debeRetornar200_cuandoEsValido() throws Exception {
        Rol cambios = new Rol(null, "NUEVO_NOMBRE");
        Rol actualizado = new Rol(1L, "NUEVO_NOMBRE");

        when(rolService.update(eq(1L), any(Rol.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cambios)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("NUEVO_NOMBRE"));
    }
}
