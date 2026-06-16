package com.semestral.productos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.dto.ProductoResponseDTO;
import com.semestral.productos.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.semestral.productos.exception.ResourceNotFoundException;


@RestController
@Tag(name = "Productos", description = "Operaciones del CRUD de productos.")
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService prodService;

    @Operation(
        summary = "Listar todos los productos que existan en la tabla.",
        description = "Retorna la lista completa de productos que esten dentro de la tabla."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de productos retornada correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProductoRequestDTO.class)
            )
        )
    })
        @GetMapping()
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductos() {
        return ResponseEntity.ok(prodService.getAllProductos());
    }


    @Operation(
        summary = "Obtener producto por ID",
        description = "Busca y retorna el producto con el ID indicado." + 
        "Puede retornar un 404 si no lo consigue en la tabla."
    )
     @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado mediante su ID.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProductoRequestDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado en la base de datos.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> encontrarPorId(@PathVariable Long id){
        return prodService.encontrarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @ApiResponses({
        @ApiResponse(
            responseCode="201",
            description="Producto creado correctamente.",
            content=@Content(
                mediaType= MediaType.APPLICATION_JSON_VALUE,
                schema=@Schema(implementation= ProductoRequestDTO.class)
            )
        ),
        @ApiResponse(
            responseCode="400",
            description="Error de validación: datos inválidos o campos faltantes.",
            content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PostMapping("/agregar")
    public ResponseEntity<ProductoResponseDTO>agregarProd(@Valid @RequestBody ProductoRequestDTO prod){
        return ResponseEntity.status(201).body(prodService.saveProducto(prod));
        
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        if (prodService.encontrarPorId(id).isEmpty()) {
            throw new ResourceNotFoundException(id);
        }
        prodService.eliminarProd(id);
        return ResponseEntity.ok("Producto eliminado correctamente.");
    }

    @Operation(
        summary = "Actualizar un precio de un producto existente.",
        description = "Modifica los precios de un producto identificado por su ID."
    )
    @ApiResponses({

        @ApiResponse(
            responseCode="201",
            description="Producto modificado correctamente.",
            content=@Content(
                mediaType= MediaType.APPLICATION_JSON_VALUE,
                schema=@Schema(implementation= ProductoRequestDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación: datos inválidos o campos faltantes.",
            content = @Content(mediaType=MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PutMapping("/{id}/precio")
    public ResponseEntity<ProductoResponseDTO> actualizarPrecio(@PathVariable Long id, @RequestBody BigDecimal precio){
        ProductoResponseDTO updated = prodService.updatePrecio(id, precio);
        return ResponseEntity.ok(updated);
    }
    @Operation(
        summary = "Actualizar una descripcion de un producto existente.",
        description = "Modifique las descripciones de los productos identificado por su ID."
    )
    @ApiResponses({

    @ApiResponse(
        responseCode="201",
        description="Producto modificado correctamente.",
        content=@Content(
        mediaType= MediaType.APPLICATION_JSON_VALUE,
        schema=@Schema(implementation= ProductoRequestDTO.class)
        )
    ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación: datos inválidos o campos faltantes.",
            content = @Content(mediaType=MediaType.APPLICATION_JSON_VALUE)
        )
    })

    @PutMapping("/{id}/descripcion")
    public ResponseEntity<ProductoResponseDTO> actualizarDescripcion(@PathVariable Long id, @RequestBody ProductoRequestDTO body){
        ProductoResponseDTO updated = prodService.updateDescripcion(id, body.getDescProd());
        return ResponseEntity.ok(updated);
    }



}
