package com.joyeria.gestion_envio.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("[EnvioService] Consultando todos los envíos registrados");
        return envioRep.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnvioResponseDTO findByIdOrThrow(Long id) {
        log.info("[EnvioService] Buscando envío con ID: {}", id);
        Envio e = envioRep.findById(id).orElseThrow(() -> {
            log.warn("[EnvioService] Envío ID {} no encontrado", id);
            return new ResourceNotFoundException("No se encontró el envío con ID: " + id);
        });
        return convertToDto(e);
    }

    @Transactional
    public EnvioResponseDTO saveEnvio(EnvioRequestDTO request) {
        log.info("[EnvioService] Iniciando creación de envío para la Venta ID: {}", request.getIdVenta());

        if (request.getIdVenta() == null || request.getIdDireccion() == null) {
            log.warn("[EnvioService] Fallo al crear envío: ID de venta o dirección son nulos");
            throw new BusinessConflictException("El ID de la venta y de la dirección son obligatorios.");
        }
        
        try {
            ventaClient.obtenerVentaPorId(request.getIdVenta());
        } catch (FeignException.NotFound e) {
            log.warn("[EnvioService] Venta ID {} no existe en ms-ventas", request.getIdVenta());
            throw new ResourceNotFoundException("No se puede registrar: La venta con ID " + request.getIdVenta() + " no existe.");
        } catch (FeignException e) {
            log.error("[EnvioService] Error de red con ms-ventas: {}", e.getMessage());
            throw new ExternalServiceException("El microservicio de Ventas no se encuentra disponible en este momento.");
        }

        try {
            direccionClient.obtenerDireccionPorId(request.getIdDireccion());
        } catch (FeignException.NotFound e) {
            log.warn("[EnvioService] Dirección ID {} no existe en ms-direcciones", request.getIdDireccion());
            throw new ResourceNotFoundException("No se puede registrar: La dirección con ID " + request.getIdDireccion() + " no existe.");
        } catch (FeignException e) {
            log.error("[EnvioService] Error de red con ms-direcciones: {}", e.getMessage());
            throw new ExternalServiceException("El microservicio de Direcciones no se encuentra disponible en este momento.");
        }

        Envio envio = new Envio();
        envio.setIdEnvio(null);
        envio.setFechaEnvio(request.getFechaEnvio() != null ? request.getFechaEnvio() : LocalDateTime.now());
        envio.setFechaRecep(request.getFechaRecepcion() != null ? request.getFechaRecepcion() : LocalDateTime.now().plusDays(3));
        envio.setIdVenta(request.getIdVenta());
        envio.setIdDireccion(request.getIdDireccion());
        envio.setEstado(request.getEstado() != null && !request.getEstado().isBlank() ? request.getEstado() : "PREPARACION");

        Envio guardado = envioRep.save(envio);
        log.info("[EnvioService] Envío creado exitosamente con ID: {}", guardado.getIdEnvio());
        return convertToDto(guardado);
    }

    @Transactional
    public EnvioResponseDTO update(Long id, EnvioRequestDTO request) {
        log.info("[EnvioService] Iniciando actualización del envío ID: {}", id);
        
        Envio existing = envioRep.findById(id).orElseThrow(() -> {
            log.warn("[EnvioService] Fallo al actualizar: Envío ID {} no encontrado", id);
            return new ResourceNotFoundException("No se encontró el envío con ID: " + id);
        });
        
        if (request.getIdVenta() != null) existing.setIdVenta(request.getIdVenta());
        if (request.getIdDireccion() != null) existing.setIdDireccion(request.getIdDireccion());
        if (request.getEstado() != null && !request.getEstado().isBlank()) existing.setEstado(request.getEstado());
        if (request.getFechaEnvio() != null) existing.setFechaEnvio(request.getFechaEnvio());
        
        if (request.getFechaRecepcion() != null) {
            if (existing.getFechaEnvio() != null && request.getFechaRecepcion().isBefore(existing.getFechaEnvio())) {
                log.warn("[EnvioService] Conflicto de fechas: Fecha de recepción anterior a la de envío");
                throw new BusinessConflictException("La fecha de recepción no puede ser anterior a la fecha de envío.");
            }
            existing.setFechaRecep(request.getFechaRecepcion());
        }

        Envio saved = envioRep.save(existing);
        log.info("[EnvioService] Envío ID {} actualizado correctamente", saved.getIdEnvio());
        return convertToDto(saved);
    }
}