package com.semestral.gestion_usuarios.controller;

import com.semestral.gestion_usuarios.dto.UsuarioRequestDTO;
import com.semestral.gestion_usuarios.dto.UsuarioResponseDTO;
import com.semestral.gestion_usuarios.service.UsuarioService;
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
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones CRUD para la administración del ciclo de vida de los usuarios")
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(
        summary = "Listar todos los usuarios",
        description = "Retorna una lista completa con todos los usuarios registrados, transformados a formato DTO seguro."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios retornada correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UsuarioResponseDTO.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        log.info("[UsuarioController] GET /api/usuarios");
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Busca en la base de datos el usuario con el ID indicado. Si no existe, retorna un error 404."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado con éxito",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UsuarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado en el sistema",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getById(
        @Parameter(
            description = "ID del usuario a buscar",
            example = "1",
            required = true
        )
        @PathVariable Long id
    ) {
        log.info("[UsuarioController] GET /api/usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.findByIdOrThrow(id));
    }

    @Operation(
        summary = "Crear nuevo usuario",
        description = "Registra un usuario en el sistema. Realiza validaciones internas de campos obligatorios, " +
                      "verifica la existencia del Rol, consulta al microservicio externo (ms-estado) para validar el estado " +
                      "y asegura que el correo electrónico no esté duplicado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UsuarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los datos de entrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rol o Estado especificado no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto: El correo electrónico ya se encuentra registrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(
        @Parameter(
            description = "Estructura JSON con los datos del nuevo usuario", 
            required = true
        )
        @Valid @RequestBody UsuarioRequestDTO req
    ) {
        log.info("[UsuarioController] POST /api/usuarios con correo: {}", req.getCorreoU());
        UsuarioResponseDTO created = usuarioService.saveUsuario(req);
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.getIdU())).body(created);
    }

    @Operation(
        summary = "Actualizar usuario por ID",
        description = "Modifica los datos de un usuario existente. Si se incluye una nueva contraseña, " +
                      "esta será encriptada automáticamente. Valida también que el nuevo correo no choque con otro usuario."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario actualizado correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UsuarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario, Rol o Estado no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto: El nuevo correo ya pertenece a otro usuario",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(
        @Parameter(
            description = "ID del usuario a actualizar", 
            example = "1", 
            required = true
        )
        @PathVariable Long id,
        @Parameter(
            description = "Estructura JSON con los campos modificados del usuario", 
            required = true
        )
        @Valid @RequestBody UsuarioRequestDTO req
    ) {
        log.info("[UsuarioController] PUT /api/usuarios/{}", id);
        UsuarioResponseDTO updated = usuarioService.update(id, req);
        return ResponseEntity.ok(updated);
    }
}