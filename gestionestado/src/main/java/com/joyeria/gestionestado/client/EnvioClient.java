package com.joyeria.gestionestado.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.joyeria.gestionestado.dto.EnvioResponseDTO;

@FeignClient(name = "ms-envio", url = "${ms.envio.url}")
public interface EnvioClient {
    @GetMapping("/api/envios")
    List<EnvioResponseDTO> obtenerTodos();
}
