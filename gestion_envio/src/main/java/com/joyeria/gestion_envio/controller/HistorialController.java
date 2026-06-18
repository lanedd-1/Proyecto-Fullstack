package com.joyeria.gestion_envio.controller;

import com.joyeria.gestion_envio.dto.HistorialRequestDTO;
import com.joyeria.gestion_envio.dto.HistorialResponseDTO;
import com.joyeria.gestion_envio.service.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/historial")
@Tag(name = "Historial", description = "Operaciones para gestionar el historial de cambios de estado de los envíos")
public class HistorialController {

    private final HistorialService historialService;

    @Operation(summary = "Registrar nuevo movimiento de historial", description = "Guarda un cambio de estado para un envío, validando al usuario responsable vía ms-usuarios.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Movimiento registrado con éxito", content = @Content(schema = @Schema(implementation = HistorialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación en la solicitud", content = @Content),
        @ApiResponse(responseCode = "404", description = "Envío o Usuario no encontrados", content = @Content),
        @ApiResponse(responseCode = "503", description = "Servicio ms-usuarios no disponible", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HistorialResponseDTO> crearHistorial(
            @Parameter(description = "Datos del nuevo movimiento en el historial", required = true) 
            @Valid @RequestBody HistorialRequestDTO request) {
        log.info("[HistorialController] POST /api/historial - Solicitud para registrar movimiento de historial");
        HistorialResponseDTO response = historialService.saveHistorial(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todo el historial", description = "Obtiene todos los registros de cambios de estado del sistema completo.")
    @ApiResponse(responseCode = "200", description = "Lista de historiales retornada exitosamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping
    public ResponseEntity<List<HistorialResponseDTO>> obtenerTodoElHistorial() {
        log.info("[HistorialController] GET /api/historial - Solicitud para listar todo el historial");
        List<HistorialResponseDTO> historial = historialService.getAllHistorial();
        return ResponseEntity.ok(historial);
    }

    @Operation(summary = "Obtener registro de historial por ID", description = "Busca un registro de movimiento de historial en específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Registro de historial no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialResponseDTO> obtenerHistorialPorId(
            @Parameter(description = "ID del registro de historial", example = "1", required = true) 
            @PathVariable Long id) {
        log.info("[HistorialController] GET /api/historial/{} - Solicitud para obtener historial por ID", id);
        HistorialResponseDTO response = historialService.findByIdOrThrow(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener historial por ID de Envío", description = "Busca todos los cambios de estado (trazabilidad) asociados a un envío específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial del envío encontrado"),
        @ApiResponse(responseCode = "404", description = "El envío solicitado no existe", content = @Content)
    })
    @GetMapping("/envio/{envioId}")
    public ResponseEntity<List<HistorialResponseDTO>> obtenerHistorialPorEnvio(
            @Parameter(description = "ID del envío para ver su trazabilidad", example = "1", required = true) 
            @PathVariable Long envioId) {
        log.info("[HistorialController] GET /api/historial/envio/{} - Solicitud para obtener historial del envío", envioId);
        List<HistorialResponseDTO> historialPorEnvio = historialService.getHistorialByEnvioId(envioId);
        return ResponseEntity.ok(historialPorEnvio);
    }
}