package com.semestral.venta.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.semestral.venta.dto.DetalleRequestDTO;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.service.DetalleService;
import com.semestral.venta.service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "operaciones CRUD para las ventas y detalles")
@RequiredArgsConstructor
public class Controller {

	private final VentaService ventaService;
	private final DetalleService detalleService;

	@Operation(
		summary = "Listar ventas", 
		description = "Devuelve todas las ventas con sus detalles."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", 
			description = "Lista de ventas devuelta correctamente"
		),
	})
	@GetMapping("/verVentas")
	public List<VentaResponseDTO> getAllVentas() {
		return ventaService.obtenerTodas();
	}

	@Operation(
		summary = "Obtener venta por id", 
		description = "Devuelve una venta específica junto con sus detalles."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200", 
			description = "Venta encontrada"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Venta no encontrada"
		),
	})
	@GetMapping("/ventas/{id}")
	public VentaResponseDTO getVenta(@PathVariable Long id) {
		return ventaService.obtenerPorId(id);
	}

	@Operation(
		summary = "Crear venta",
	    description = "Crea una venta nueva y devuelve el registro creado."
)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Venta creada correctamente"
		),
		@ApiResponse(
			responseCode = "400",
			description = "Solicitud inválida"
		)
	})
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public VentaResponseDTO createVenta(@RequestBody @Valid VentaRequestDTO dto) {
		return ventaService.crearVenta(dto);
	}

	@Operation(
		summary = "Listar detalles", 
		description = "Devuelve todos los detalles de ventas registrados."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Lista de detalles devuelta correctamente"
		)
	})
	@GetMapping("/verDetalles")
	public List<DetalleResponseDTO> getAllDetalles() {
		return detalleService.obtenerTodos();
	}

	@Operation(
		summary = "Obtener detalle por id", 
		description = "Devuelve un detalle específico de venta."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
		 description = "Detalle encontrado"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Detalle no encontrado"
		),
	})
	@GetMapping("/detalles/{id}")
	public DetalleResponseDTO getDetalle(@PathVariable Long id) {
		return detalleService.obtenerPorId(id);
	}

	@Operation(
		summary = "Crear detalle", 
		description = "Crea un nuevo detalle asociado a una venta."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201", 
			description = "Detalle creado correctamente"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Solicitud inválida"
		),
	})
	@PostMapping("/detalles/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public DetalleResponseDTO createDetalle(@RequestBody @Valid DetalleRequestDTO dto) {
		return detalleService.crearDetalle(dto);
	}

	@Operation(
		summary = "Actualizar detalle", 
		description = "Actualiza los datos de un detalle existente."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
		    description = "Detalle actualizado correctamente"
		),
		@ApiResponse(
			responseCode = "400",
		    description = "Solicitud inválida"
		),
		@ApiResponse(
			responseCode = "404",
		    description = "Detalle no encontrado"
		),
	})
	@PutMapping("/detalles/{id}")
	public DetalleResponseDTO updateDetalle(@PathVariable Long id, @RequestBody @Valid DetalleRequestDTO dto) {
		return detalleService.actualizarDetalle(id, dto);
	}

	@Operation(
		summary = "Eliminar detalle",
		description = "Elimina un detalle específico de una venta."
	)
	@ApiResponses({

		@ApiResponse(
		    responseCode = "204", 
			description = "Detalle eliminado correctamente"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Detalle no encontrado"
		),

	})
	@DeleteMapping("/detalles/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDetalle(@PathVariable Long id) {
		detalleService.eliminarDetalle(id);
	}


}
