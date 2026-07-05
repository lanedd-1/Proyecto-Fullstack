package com.semestral.venta.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.venta.client.ProductoClient;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.exception.ResourceNotFoundException;
import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.VentaRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRe;
    private final ProductoClient productoClient;

    public List<VentaResponseDTO> obtenerTodas() {
        log.info("Consultando todas las ventas");
        return ventaRe.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public VentaResponseDTO obtenerPorId(Long id) {
        log.info("Buscando venta con ID: {}", id);
        Venta venta = ventaRe.findById(id)
            .orElseThrow(() -> {
                log.warn("Venta ID {} no encontrada", id);
                return new ResourceNotFoundException(id);
            });
        return convertToDTO(venta);
    }

    public VentaResponseDTO crearVenta(VentaRequestDTO dto) {
        log.info("Iniciando creación de nueva venta");
        Venta venta = new Venta();
        venta.setFechaV(parseFecha(dto.getFechaV()));
        venta.setTotal(0.0);
        Venta nuevaVenta = ventaRe.save(venta);
        log.info("Venta creada exitosamente con ID: {}", nuevaVenta.getIdVenta());
        return convertToDTO(nuevaVenta);
    }

    public VentaResponseDTO convertToDTO(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setFechaV(venta.getFechaV() != null ? venta.getFechaV().toString() : null);
        dto.setTotal(venta.getTotal() != null ? venta.getTotal() : 0.0);
        dto.setDetalles(convertDetalles(venta.getDetalles()));
        return dto;
    }

    private List<DetalleResponseDTO> convertDetalles(List<Detalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return new ArrayList<>();
        }
        return detalles.stream()
            .map(this::convertDetalleToDTO)
            .collect(Collectors.toList());
    }

    private DetalleResponseDTO convertDetalleToDTO(Detalle detalle) {
        DetalleResponseDTO dto = new DetalleResponseDTO(
            detalle.getIdDetalle(),
            detalle.getCantidad(),
            detalle.getSubTotal(),
            detalle.getIdVenta() != null ? detalle.getIdVenta().getIdVenta() : null,
            detalle.getProductoId(),
            null
        );
        if (detalle.getProductoId() != null) {
            try {
                Map<String, Object> producto = productoClient.obtenerPorId(detalle.getProductoId());
                dto.setProductoNombre(producto != null ? String.valueOf(producto.get("nombreProd")) : null);
            } catch (FeignException e) {
                log.warn("No se pudo obtener el nombre del producto ID {} desde ms-productos: {}", detalle.getProductoId(), e.getMessage());
            } catch (Exception e) {
                log.error("Error inesperado al mapear el producto ID {}: {}", detalle.getProductoId(), e.getMessage());
            }
        }
        return dto;
    }

    private LocalDateTime parseFecha(String fechaV) {
        if (fechaV == null || fechaV.isBlank()) {
            throw new IllegalArgumentException("fechaV es obligatorio y no puede estar vacío");
        }
        try {
            return LocalDateTime.parse(fechaV);
        } catch (DateTimeParseException ex) {
            log.warn("Error al parsear la fecha ingresada: {}", fechaV);
            throw new IllegalArgumentException("fechaV debe tener formato YYYY-MM-DDTHH:mm:ss, por ejemplo 2024-05-22T14:30:00", ex);
        }
    }
}