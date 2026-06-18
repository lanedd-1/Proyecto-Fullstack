package com.joyeria.gestion_envio.controller;

import com.joyeria.gestion_envio.dto.EnvioRequestDTO;
import com.joyeria.gestion_envio.dto.EnvioResponseDTO;
import com.joyeria.gestion_envio.service.EnvioService;
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
@RequestMapping("/api/envios")
@Tag(name = "Envíos", description = "Operaciones CRUD para la gestión de envíos de ventas")
public class EnvioController {

    private final EnvioService envioService;

    @Operation(summary = "Listar todos los envíos", description = "Obtiene una lista completa de los envíos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de envíos retornada exitosamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> obtenerTodos() {
        log.info("[EnvioController] GET /api/envios - Solicitud para listar todos los envíos");
        List<EnvioResponseDTO> envios = envioService.getAllEnvios();
        return ResponseEntity.ok(envios);
    }

    @Operation(summary = "Obtener envío por ID", description = "Busca los detalles de un envío específico mediante su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envío encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorId(
            @Parameter(description = "ID del envío", example = "1", required = true) 
            @PathVariable Long id) {
        log.info("[EnvioController] GET /api/envios/{} - Solicitud para obtener envío por ID", id);
        EnvioResponseDTO response = envioService.findByIdOrThrow(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear nuevo envío", description = "Registra un envío validando la existencia de la Venta (ms-ventas) y la Dirección (ms-direcciones).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Envío creado correctamente", content = @Content(schema = @Schema(implementation = EnvioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación en la solicitud", content = @Content),
        @ApiResponse(responseCode = "404", description = "Venta o Dirección no existen en los microservicios externos", content = @Content),
        @ApiResponse(responseCode = "503", description = "Servicio externo no disponible", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(
            @Parameter(description = "Datos del envío a crear", required = true) 
            @Valid @RequestBody EnvioRequestDTO request) {
        log.info("[EnvioController] POST /api/envios - Solicitud para crear un nuevo envío");
        EnvioResponseDTO response = envioService.saveEnvio(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar envío", description = "Actualiza los datos de un envío existente, como sus fechas o estado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envío actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto en las fechas ingresadas", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @Parameter(description = "ID del envío a actualizar", example = "1", required = true) 
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del envío", required = true) 
            @Valid @RequestBody EnvioRequestDTO request) {
        log.info("[EnvioController] PUT /api/envios/{} - Solicitud para actualizar envío", id);
        EnvioResponseDTO response = envioService.update(id, request);
        return ResponseEntity.ok(response);
    }
}