package com.joyeria.gestionestado.controller;

import java.util.List;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


import com.fasterxml.jackson.databind.ObjectMapper;


import com.joyeria.gestionestado.Service.EstadoService;


@WebMvcTest(EstadoController.class)
@DisplayName("Tests del EstadoController con MockMvc")
public class EstadoControllerTest {

    //Simula peticiones HTTP sin levantar un servidor real
    @Autowired
    private MockMvc mockMvc;

    //Mock del service inyectado en el controller
    @MockitoBean
    private EstadoService estadoService;

    //Convierte objetos Java a JSON para los endpoints
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/estados debe retornar un JSON con la lista de estados y codigo 200")
    void getAll_debeRetornar200ConListaDeEstados() throws Exception{
        EstadoResponseDTO dto = new EstadoResponseDTO(1L, "Activo");
        when(estadoService.obtenerTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/estados")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombreEstado").value("Activo"))
                .andExpect(jsonPath("$[0].idEstado").value(1));
    }

    @Test
    @DisplayName("POST /api/estados debe retornar 201 con datos validos")
    void crearEstado_debeRetornar201_cuandoDatosValidos() throws Exception {
        EstadoRequestDTO request = new EstadoRequestDTO("Pendiente");
        EstadoResponseDTO response = new EstadoResponseDTO(2L, "Pendiente");
        when(estadoService.saveEstado(any(EstadoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/estados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEstado").value(2))
                .andExpect(jsonPath("$.nombreEstado").value("Pendiente"));
    }

}
