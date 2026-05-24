package com.joyeria.gestion_configuracion.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.joyeria.gestion_configuracion.dto.UsuarioResponseDTO;

@FeignClient(name = "ms-usuario", url = "${ms.usuario.url}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios")
    List<UsuarioResponseDTO> getAllUsuarios();

}
