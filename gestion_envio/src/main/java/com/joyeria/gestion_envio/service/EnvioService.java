package com.joyeria.gestion_envio.service;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyeria.gestion_envio.client.DireccionClient;
import com.joyeria.gestion_envio.client.VentaClient;
import com.joyeria.gestion_envio.dto.EnvioRequestDTO;
import com.joyeria.gestion_envio.dto.EnvioResponseDTO;
import com.joyeria.gestion_envio.exception.BusinessConflictException;
import com.joyeria.gestion_envio.exception.ExternalServiceException;
import com.joyeria.gestion_envio.exception.ResourceNotFoundException;
import com.joyeria.gestion_envio.model.Envio;
import com.joyeria.gestion_envio.repository.EnvioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EnvioService {

    private final EnvioRepository envioRep;
    private final VentaClient ventaClient;         
    private final DireccionClient direccionClient; 

    private EnvioResponseDTO convertToDto(Envio e) {
        if (e == null) return null;
        return new EnvioResponseDTO(
                e.getIdEnvio(), e.getFechaEnvio(), e.getFechaRecep(),
                e.getIdVenta(), e.getIdDireccion(), e.getEstado()
        );
    }

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> getAllEnvios() {
        return envioRep.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnvioResponseDTO findByIdOrThrow(Long id) {
        Envio e = envioRep.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró el envío con ID: " + id));
        return convertToDto(e);
    }

    @Transactional
    public EnvioResponseDTO saveEnvio(EnvioRequestDTO request) {
        if (request.getIdVenta() == null || request.getIdDireccion() == null) {
            throw new BusinessConflictException("El ID de la venta y de la dirección son obligatorios.");
        }
        
        /* BYPASS TEMPORAL: Desactivado hasta que existan los MS de Ventas y Direcciones
        try {
            ventaClient.obtenerVentaPorId(request.getIdVenta());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("No se puede registrar: La venta con ID " + request.getIdVenta() + " no existe.");
        } catch (FeignException e) {
            throw new ExternalServiceException("Error de comunicación con el servicio de Ventas.");
        }
        */
        try {
            direccionClient.obtenerDireccionPorId(request.getIdDireccion());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("No se puede registrar: La dirección con ID " + request.getIdDireccion() + " no existe.");
        } catch (FeignException e) {
            throw new ExternalServiceException("Error de comunicación con el servicio de Direcciones.");
        }

        Envio envio = new Envio();
        envio.setIdEnvio(null);
        envio.setFechaEnvio(request.getFechaEnvio() != null ? request.getFechaEnvio() : LocalDateTime.now());
        
        // --- LA LÍNEA CORREGIDA ---
        // Si no mandan fecha de recepción, calculamos 3 días a partir de hoy
        envio.setFechaRecep(request.getFechaRecepcion() != null ? request.getFechaRecepcion() : LocalDateTime.now().plusDays(3));
        
        envio.setIdVenta(request.getIdVenta());
        envio.setIdDireccion(request.getIdDireccion());
        envio.setEstado(request.getEstado() != null && !request.getEstado().isBlank() ? request.getEstado() : "PREPARACION");

        Envio guardado = envioRep.save(envio);
        return convertToDto(guardado);
    }

    @Transactional
    public EnvioResponseDTO update(Long id, EnvioRequestDTO request) {
        Envio existing = envioRep.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró el envío con ID: " + id));
        if (request.getIdVenta() != null) existing.setIdVenta(request.getIdVenta());
        if (request.getIdDireccion() != null) existing.setIdDireccion(request.getIdDireccion());
        if (request.getEstado() != null && !request.getEstado().isBlank()) existing.setEstado(request.getEstado());
        if (request.getFechaEnvio() != null) existing.setFechaEnvio(request.getFechaEnvio());
        if (request.getFechaRecepcion() != null) {
            if (existing.getFechaEnvio() != null && request.getFechaRecepcion().isBefore(existing.getFechaEnvio())) {
                throw new BusinessConflictException("La fecha de recepción no puede ser anterior a la fecha de envío.");
            }
            existing.setFechaRecep(request.getFechaRecepcion());
        }

        Envio saved = envioRep.save(existing);
        return convertToDto(saved);
    }
}