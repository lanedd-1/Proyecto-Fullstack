package com.joyeria.gestion_envio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyeria.gestion_envio.dto.HistorialRequestDTO;
import com.joyeria.gestion_envio.dto.HistorialResponseDTO;
import com.joyeria.gestion_envio.service.HistorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;

    @PostMapping
    public ResponseEntity<HistorialResponseDTO> crearHistorial(@Valid @RequestBody HistorialRequestDTO request) {
        HistorialResponseDTO response = historialService.saveHistorial(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HistorialResponseDTO>> obtenerTodoElHistorial() {
        List<HistorialResponseDTO> historial = historialService.getAllHistorial();
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialResponseDTO> obtenerHistorialPorId(@PathVariable Long id) {
        HistorialResponseDTO response = historialService.findByIdOrThrow(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/envio/{envioId}")
    public ResponseEntity<List<HistorialResponseDTO>> obtenerHistorialPorEnvio(@PathVariable Long envioId) {
        List<HistorialResponseDTO> historialPorEnvio = historialService.getHistorialByEnvioId(envioId);
        return ResponseEntity.ok(historialPorEnvio);
    }
}