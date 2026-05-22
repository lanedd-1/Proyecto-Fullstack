package com.joyeria.gestion_envio.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyeria.gestion_envio.dto.EnvioRequestDTO;
import com.joyeria.gestion_envio.dto.EnvioResponseDTO;
import com.joyeria.gestion_envio.service.EnvioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> obtenerTodos() {
        List<EnvioResponseDTO> envios = envioService.getAllEnvios();
        return ResponseEntity.ok(envios);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorId(@PathVariable Long id) {
        EnvioResponseDTO response = envioService.findByIdOrThrow(id);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(@Valid @RequestBody EnvioRequestDTO request) {
        EnvioResponseDTO response = envioService.saveEnvio(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(@PathVariable Long id, @Valid @RequestBody EnvioRequestDTO request) {
        EnvioResponseDTO response = envioService.update(id, request);
        return ResponseEntity.ok(response);
    }
}