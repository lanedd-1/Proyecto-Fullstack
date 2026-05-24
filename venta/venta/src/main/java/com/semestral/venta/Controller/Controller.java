package com.semestral.venta.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

	private final VentaService ventaService;
	private final DetalleService detalleService;

	// Ventas
	@GetMapping("/verVentas")
	public List<VentaResponseDTO> getAllVentas() {
		return ventaService.obtenerTodas();
	}

	@GetMapping("/ventas/{id}")
	public VentaResponseDTO getVenta(@PathVariable Long id) {
		return ventaService.obtenerPorId(id);
	}

	@PostMapping("/ventas/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public VentaResponseDTO createVenta(@RequestBody @Valid VentaRequestDTO dto) {
		return ventaService.crearVenta(dto);
	}

	// Detalles
	@GetMapping("/verDetalles")
	public List<DetalleResponseDTO> getAllDetalles() {
		return detalleService.obtenerTodos();
	}

	@GetMapping("/detalles/{id}")
	public DetalleResponseDTO getDetalle(@PathVariable Long id) {
		return detalleService.obtenerPorId(id);
	}

	@PostMapping("/detalles/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public DetalleResponseDTO createDetalle(@RequestBody @Valid DetalleRequestDTO dto) {
		return detalleService.crearDetalle(dto);
	}

	@PutMapping("/detalles/{id}")
	public DetalleResponseDTO updateDetalle(@PathVariable Long id, @RequestBody @Valid DetalleRequestDTO dto) {
		return detalleService.actualizarDetalle(id, dto);
	}


}
