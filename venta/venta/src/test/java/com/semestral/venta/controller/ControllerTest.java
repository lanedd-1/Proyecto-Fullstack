package com.semestral.venta.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.semestral.venta.Controller.Controller;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.service.DetalleService;
import com.semestral.venta.service.VentaService;

@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private DetalleService detalleService;

    @Test
    void getAllVentas_shouldReturnList() throws Exception {
        VentaResponseDTO v = new VentaResponseDTO();
        v.setIdVenta(1L);

        when(ventaService.obtenerTodas()).thenReturn(List.of(v));

        mockMvc.perform(get("/api/ventas/verVentas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].idVenta").value(1));
    }
}
