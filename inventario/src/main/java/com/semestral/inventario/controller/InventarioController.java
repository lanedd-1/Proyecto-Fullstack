package com.semestral.inventario.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.inventario.dto.EstanteRequestDTO;
import com.semestral.inventario.dto.DescontarProductoRequestDTO;
import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.dto.PasilloRequestDTO;
import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
@Tag(name = "Inventario", description = "Operaciones del CRUD de inventario.")
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService invService;

    @Operation(
        summary = "Listar todas las ubicaciones de los productos existentes",
        description = "Retorna la lista de las ubicaciones de los productos que esten ingresados en el producto y en el inventario"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de productos retornada correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InventarioResponseDTO.class)
            )
        )
    })
    @GetMapping() 
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodoElStock() {
        return ResponseEntity.ok(invService.getTodoStock());
    }
    
    @Operation(
        summary = "Obtener stock de un producto específico",
        description = "Retorna la lista de ubicaciones donde se encuentra almacenado un producto específico identificado por su ID, incluyendo nombre, precio, SKU y cantidad disponible"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Stock del producto retornado correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InventarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado en el microservicio de productos"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerStockPorProducto(
        @PathVariable("id") Long id) {
        return ResponseEntity.ok(invService.getStockPorProducto(id));
    }

    @Operation(
        summary = "Agregar stock a una ubicación",
        description = "Incrementa la cantidad de stock de un producto en una ubicación específica (pasillo y estante). Si el producto no existe en esa ubicación, crea un nuevo registro"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Stock agregado correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InventarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos en la solicitud"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pasillo, estante o producto no encontrado"
        )
    })
    @PutMapping("/agregar")
    public ResponseEntity<InventarioResponseDTO> agregarStock(@RequestBody InventarioRequestDTO request) {
        return ResponseEntity.ok(invService.agregarStock(request));
    }

    @Operation(
        summary = "Descontar stock de una ubicación",
        description = "Reduce la cantidad de stock de un producto en una ubicación específica. Valida que haya suficiente stock disponible antes de realizar el descuento"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Stock descontado correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InventarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Stock insuficiente para completar la operación"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto, ubicación o pasillo/estante no encontrado"
        )
    })
    @PutMapping("/descontar")
    public ResponseEntity<InventarioResponseDTO> descontarStock(@RequestBody InventarioRequestDTO request) {
        return ResponseEntity.ok(invService.descontarStock(request));
    }

    @Operation(
        summary = "Descontar stock por producto",
        description = "Reduce el stock de cualquier ubicación que tenga suficientes unidades de un producto específico. Si no hay suficiente stock disponible se devuelve un error."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Stock descontado correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InventarioResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Stock insuficiente para completar la operación"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        )
    })
    @PutMapping("/descontar/producto")
    public ResponseEntity<InventarioResponseDTO> descontarStockPorProducto(@RequestBody DescontarProductoRequestDTO request) {
        return ResponseEntity.ok(invService.descontarStockPorProducto(request));
    }

    @Operation(
        summary = "Crear un nuevo pasillo",
        description = "Crea un nuevo pasillo en el almacén. Los pasillos son divisiones principales del almacén donde se organizan los estantes"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Pasillo creado correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Pasillo.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos para crear el pasillo"
        )
    })
    @PostMapping("/pasillos")
    public ResponseEntity<Pasillo> crearPasillo(@RequestBody PasilloRequestDTO request) {
        return ResponseEntity.ok(invService.crearPasillo(request));
    }

    @Operation(
        summary = "Crear un nuevo estante",
        description = "Crea un nuevo estante en el almacén. Los estantes se ubican dentro de los pasillos y almacenan los productos"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estante creado correctamente.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Estante.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos para crear el estante"
        )
    })
    @PostMapping("/estantes")
    public ResponseEntity<Estante> crearEstante(@RequestBody EstanteRequestDTO request) {
        return ResponseEntity.ok(invService.crearEstante(request));
    }





}

