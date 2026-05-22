package com.semestral.venta.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.venta.exception.ResourceNotFoundException;

import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.model.Venta;

import com.semestral.venta.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRe;

    public List<VentaResponseDTO> obtenerTodas() {

        return ventaRe.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public VentaResponseDTO obtenerPorId(Long id) {

        Venta venta = ventaRe.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
        return convertToDTO(venta);
    }

    public VentaResponseDTO crearVenta(VentaRequestDTO dto) {

    Venta venta = new Venta();
    venta.setFechaV(parseFecha(dto.getFechaV()));
    venta.setTotal(dto.getTotal());
    
    Venta nuevaVenta = ventaRe.save(venta);
    return convertToDTO(nuevaVenta);
}

    public VentaResponseDTO convertToDTO(Venta venta) {

        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setFechaV(venta.getFechaV() != null ? venta.getFechaV().toString() : null);
        dto.setTotal(venta.getTotal());
        return dto;
    }

    private LocalDateTime parseFecha(String fechaV) {

        if (fechaV == null || fechaV.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(fechaV);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("fechaV debe tener formato YYYY/MM/DD, por ejemplo 2024-05-22T14:30:00", ex);
        }
    }
}
