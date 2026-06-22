package com.joyeria.gestionestado.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyeria.gestionestado.Service.EstadoService;
import com.joyeria.gestionestado.dto.EstadoConEnviosResponseDTO;
import com.joyeria.gestionestado.dto.EstadoRequestDTO;
import com.joyeria.gestionestado.dto.EstadoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/estados")
@Tag(name = "Estados", description = "Opreacion de gestion de estados de envio")
public class EstadoController {

    private final EstadoService estadoService;

    @Operation(
        summary = "Listar todos los estados",
        description = "Retorna la lista completa de estados existentes en la tabla"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de estados retornada correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EstadoResponseDTO.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<EstadoResponseDTO>> getAll() {
        return ResponseEntity.ok(estadoService.obtenerTodos());
    }

    @Operation(
        summary = "Obtener estado por ID",
        description = "Busca y retorna el estado con el ID indicado" +
        "Puede retornar un 404 si no consigue en la tabla"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado encontrado mediante ID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EstadoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado en la base de datos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstadoResponseDTO> getById(
        @Parameter(
            description = "Id del estado a buscar",
            example = "1",
            required = true
        )
        @PathVariable Long id
    ) {
        EstadoResponseDTO dto = estadoService.obtenerPorId(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
        summary = "Obtener el estado y envio por ID",
        description = "Busca y retorna el estado y consulta ms-envio con el ID indicado" +
        "Puede retornar un 404 si no consigue en la tabla"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado y Envio retornados mediante su ID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EstadoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado en la base de datos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/{id}/con-envios")
    public ResponseEntity<EstadoConEnviosResponseDTO> getByIdConEnvios(
        @Parameter(
            description = "ID del estado a buscar con el envio",
            example = "1",
            required = true
        )
        @PathVariable Long id
    ) {
        EstadoConEnviosResponseDTO dto = estadoService.obtenerConEnvios(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
        summary = "Crea nuevo un estado ",
        description = "Crea un nuevo estado de envio, Retorna 409 si ya existe un estado con el mismo nombre"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Estado creado con exito",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EstadoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Error de validación. Datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe un estado con el mismo nombre",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PostMapping 
    public ResponseEntity<EstadoResponseDTO> crearEstado(@Valid @RequestBody EstadoRequestDTO request) {
        EstadoResponseDTO response = estadoService.saveEstado(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Operation(
        summary = "Actualizar un estado existente",
        description = "Actualiza el nombre del estado con el ID indicado. Retorna 404 si no existe, " +
        "o 409 si el nuevo nombre ya lo tiene otro estado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EstadoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación: datos inválidos o campos faltantes",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estado no encontrado en la base de datos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe otro estado con el mismo nombre",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PutMapping("/{id}") 
    public ResponseEntity<EstadoResponseDTO> update(
         @Parameter(
            description = "ID del estado a actualizar",
            example = "1",
            required = true
        )
        @PathVariable Long id, @Valid @RequestBody EstadoRequestDTO req
    ) {
        EstadoResponseDTO updated = estadoService.update(id, req);
        return ResponseEntity.ok(updated);
    } 
}
