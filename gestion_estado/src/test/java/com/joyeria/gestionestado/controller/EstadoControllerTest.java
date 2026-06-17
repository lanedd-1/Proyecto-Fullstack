package com.joyeria.gestionestado.controller;

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


}
