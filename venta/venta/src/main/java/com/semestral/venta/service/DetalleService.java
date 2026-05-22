package com.semestral.venta.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.venta.exception.ResourceNotFoundException;

import com.semestral.venta.dto.DetalleRequestDTO;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.DetalleRepository;
import com.semestral.venta.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleService {

    private final DetalleRepository detalleRe;
    private final VentaRepository ventaRe;

    public List<DetalleResponseDTO> obtenerTodos() {
        return detalleRe.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public DetalleResponseDTO obtenerPorId(Long id) {
        Detalle d = detalleRe.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
        return convertToDTO(d);
    }

    public DetalleResponseDTO crearDetalle(DetalleRequestDTO dto) {
        if (dto.getVentaId() == null) {
            throw new IllegalArgumentException("ventaId es obligatorio para crear un detalle");
        }
        Venta venta = ventaRe.findById(dto.getVentaId())
            .orElseThrow(() -> new ResourceNotFoundException(dto.getVentaId()));

        Detalle d = new Detalle();
        d.setCantidad(dto.getCantidad());
        d.setSubTotal(dto.getSubTotal());
        d.setIdVenta(venta);
        Detalle nuevo = detalleRe.save(d);
        return convertToDTO(nuevo);
    }

    public DetalleResponseDTO actualizarDetalle(Long id, DetalleRequestDTO dto) {
        Detalle existente = detalleRe.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Detalle no encontrado con id: " + id));
        existente.setCantidad(dto.getCantidad());
        existente.setSubTotal(dto.getSubTotal());
        if (dto.getVentaId() != null) {
            Venta venta = ventaRe.findById(dto.getVentaId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getVentaId()));
            existente.setIdVenta(venta);
        }
        Detalle actualizado = detalleRe.save(existente);
        return convertToDTO(actualizado);
    }


    public DetalleResponseDTO convertToDTO(Detalle d) {
        return new DetalleResponseDTO(
            d.getIdDetalle(),
            d.getCantidad(),
            d.getSubTotal()
        );
    }
}
