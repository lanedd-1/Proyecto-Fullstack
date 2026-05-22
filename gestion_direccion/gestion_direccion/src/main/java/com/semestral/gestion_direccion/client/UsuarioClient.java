package com.semestral.gestion_direccion.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "${ms.usuario.url}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    Object obtenerUsuarioPorId(@PathVariable("id") Long id);
}