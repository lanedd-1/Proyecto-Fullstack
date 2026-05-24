package com.joyeria.gestion_configuracion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyeria.gestion_configuracion.dto.ConfiguracionRequestDTO;
import com.joyeria.gestion_configuracion.dto.ConfiguracionResponseDTO;
import com.joyeria.gestion_configuracion.service.ConfiguracionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {
     private final ConfiguracionService configuracionService;

    @GetMapping
    public ResponseEntity<ConfiguracionResponseDTO> get() {
        return ResponseEntity.ok(configuracionService.getConfiguracion());
    }

    @PutMapping
    public ResponseEntity<ConfiguracionResponseDTO> update(@Valid @RequestBody ConfiguracionRequestDTO req) {
        return ResponseEntity.ok(configuracionService.update(req));
    }
}
