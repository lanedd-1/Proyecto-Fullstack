package com.semestral.productos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.productos.dto.CategoriaRequestDTO;
import com.semestral.productos.dto.CategoriaResponseDTO;
import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import com.semestral.productos.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService catServ;

    @Operation(
        summary = "Lista todas las categorias",
        description = "Retorna la lista completa de categorias que existen en la tabla"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de categorias retornada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CategoriaRequestDTO.class)
            )
        )

    })
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> obtenerCategorias() {
        return ResponseEntity.ok(catServ.findAllCat());
    }

    @Operation(
        summary = "Crear Categoria nueva",
        description = "Crea la categoria nueva a la tabla"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Cateegoria creada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CategoriaRequestDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validacion: datos inválidos o campos faltantes",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
        
    })
    @PostMapping("/agregarCat")
    public ResponseEntity<CategoriaResponseDTO>agregarCat(@Valid @RequestBody CategoriaRequestDTO cat){
        return ResponseEntity.status(201).body(catServ.saveCat(cat));
        
    }
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Categoria encontrada mediante su ID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CategoriaRequestDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoria no encontrada en la BD porfavor verificar el ID ingresado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> encontrarPorId(@PathVariable Long idCat){
        return catServ.buscarPorId(idCat)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(idCat));
    }

}
