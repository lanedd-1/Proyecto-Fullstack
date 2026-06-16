package com.semestral.gestion_usuarios.controller;

import com.semestral.gestion_usuarios.model.Rol;
import com.semestral.gestion_usuarios.service.RolService;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Operaciones CRUD para la gestión de los roles de acceso")
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    @Operation(
        summary = "Listar todos los roles",
        description = "Retorna la lista completa de roles existentes en la base de datos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de roles retornada correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Rol.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        log.info("[RolController] GET /api/roles");
        return ResponseEntity.ok(rolService.findAll());
    }

    @Operation(
        summary = "Obtener rol por ID",
        description = "Busca y retorna el rol correspondiente al ID indicado. Puede retornar un 404 si no existe."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Rol encontrado mediante su ID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Rol.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rol no encontrado en la base de datos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(
        @Parameter(
            description = "ID del rol a buscar",
            example = "1",
            required = true
        )
        @PathVariable Long id
    ) {
        log.info("[RolController] GET /api/roles/{}", id);
        Rol rol = rolService.getById(id);
        return ResponseEntity.ok(rol);
    }

    @Operation(
        summary = "Crear nuevo rol",
        description = "Registra un nuevo rol en el sistema. Validará que el nombre no se encuentre duplicado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Rol creado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Rol.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto: Ya existe un rol con ese nombre",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PostMapping("/guardar")
    public ResponseEntity<?> crearRol(
        @Parameter(description = "Objeto con los datos del rol a crear", required = true)
        @RequestBody Rol rol
    ) {
        log.info("[RolController] POST /api/roles/guardar");
        try {
            Rol nuevoRol = rolService.create(rol);
            return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("[RolController] Error al crear rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el rol: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Actualizar rol",
        description = "Actualiza el nombre de un rol existente. Retorna error si el nombre nuevo ya está en uso."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Rol actualizado correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Rol.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rol no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(
        @Parameter(description = "ID del rol a actualizar", example = "1", required = true)
        @PathVariable Long id,
        @Parameter(description = "Objeto con los nuevos datos del rol", required = true)
        @Valid @RequestBody Rol rol
    ) {
        log.info("[RolController] PUT /api/roles/{}", id);
        Rol updated = rolService.update(id, rol);
        return ResponseEntity.ok(updated);
    }
}