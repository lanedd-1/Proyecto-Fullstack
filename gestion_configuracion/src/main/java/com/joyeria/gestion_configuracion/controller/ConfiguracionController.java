package com.joyeria.gestion_configuracion.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyeria.gestion_configuracion.dto.ConfiguracionRequestDTO;
import com.joyeria.gestion_configuracion.dto.ConfiguracionResponseDTO;
import com.joyeria.gestion_configuracion.service.ConfiguracionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "Configuracion", description = "Operaciones de gestión de la configuración de contraseñas")
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

     private final ConfiguracionService configuracionService;

    @Operation(
        summary = "Obtener la configuración de contraseña",
        description = "Retorna la única configuración existente en la tabla junto con la lista " +
        "de usuarios obtenida desde ms-usuario (via Feign)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Configuración retornada correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ConfiguracionResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No existe configuración cargada en la base de datos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping
    public ResponseEntity<ConfiguracionResponseDTO> get() {
        return ResponseEntity.ok(configuracionService.getConfiguracion());
    }

    @Operation(
        summary = "Actualizar la configuración de contraseña",
        description = "Actualiza las reglas de validación de contraseña (longitud, mayúsculas, " +
        "minúsculas, números, caracteres especiales). Retorna 400 si la longitud mínima " +
        "es mayor o igual a la máxima"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Configuración actualizada correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ConfiguracionResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación: datos inválidos o longitud mínima inválida",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No existe configuración cargada en la base de datos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PutMapping
    public ResponseEntity<ConfiguracionResponseDTO> update(@Valid @RequestBody ConfiguracionRequestDTO req) {
        return ResponseEntity.ok(configuracionService.update(req));
    }
}
