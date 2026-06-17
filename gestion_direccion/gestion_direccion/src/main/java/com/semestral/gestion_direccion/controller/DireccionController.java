package com.semestral.gestion_direccion.controller;

import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.service.DireccionService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/direcciones")
@Tag(name = "Direcciones", description = "Operaciones CRUD para la gestión de las direcciones físicas de los usuarios")
public class DireccionController {

    private final DireccionService direccionService;

    @Operation(
        summary = "Listar todas las direcciones",
        description = "Obtiene una lista completa de las direcciones registradas, incluyendo el nombre de su Comuna y Región."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de direcciones retornada correctamente",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DireccionResponseDTO.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<DireccionResponseDTO>> getAll() {
        log.info("[DireccionController] GET /api/direcciones");
        return ResponseEntity.ok(direccionService.findAll());
    }

    @Operation(
        summary = "Obtener dirección por ID",
        description = "Busca y retorna los detalles de una dirección específica mediante su identificador."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dirección encontrada", content = @Content(schema = @Schema(implementation = DireccionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Dirección no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> getById(
        @Parameter(description = "ID de la dirección", example = "1", required = true)
        @PathVariable Long id
    ) {
        log.info("[DireccionController] GET /api/direcciones/{}", id);
        return ResponseEntity.ok(direccionService.findByIdOrThrow(id));
    }

    @Operation(
        summary = "Crear nueva dirección",
        description = "Registra una nueva dirección validando que el ID del Usuario y el ID del Estado existan en sus respectivos microservicios."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Dirección creada con éxito", content = @Content(schema = @Schema(implementation = DireccionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación en el cuerpo de la petición", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario, Estado o Comuna no encontrados", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DireccionResponseDTO> create(
        @Parameter(description = "Objeto JSON con los datos de la nueva dirección", required = true)
        @Valid @RequestBody DireccionRequestDTO req
    ) {
        log.info("[DireccionController] POST /api/direcciones");
        DireccionResponseDTO created = direccionService.create(req);
        return ResponseEntity.created(URI.create("/api/direcciones/" + created.getIdDireccion())).body(created);
    }

    @Operation(
        summary = "Actualizar dirección",
        description = "Modifica los datos de una dirección existente. Vuelve a validar dependencias externas si son modificadas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dirección actualizada correctamente", content = @Content(schema = @Schema(implementation = DireccionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado (Dirección, Comuna, Usuario o Estado)", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> update(
        @Parameter(description = "ID de la dirección a actualizar", example = "1", required = true)
        @PathVariable Long id,
        @Parameter(description = "Datos modificados de la dirección", required = true)
        @Valid @RequestBody DireccionRequestDTO req
    ) {
        log.info("[DireccionController] PUT /api/direcciones/{}", id);
        DireccionResponseDTO updated = direccionService.update(id, req);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Eliminar dirección",
        description = "Borra físicamente una dirección de la base de datos."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Dirección eliminada correctamente (Sin contenido)"),
        @ApiResponse(responseCode = "404", description = "Dirección a eliminar no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID de la dirección a eliminar", example = "1", required = true)
        @PathVariable Long id
    ) {
        log.info("[DireccionController] DELETE /api/direcciones/{}", id);
        direccionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}