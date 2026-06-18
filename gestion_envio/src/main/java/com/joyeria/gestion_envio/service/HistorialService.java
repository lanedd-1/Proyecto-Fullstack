package com.joyeria.gestion_envio.service;

import com.joyeria.gestion_envio.client.UsuarioClient;
import com.joyeria.gestion_envio.dto.HistorialRequestDTO;
import com.joyeria.gestion_envio.dto.HistorialResponseDTO;
import com.joyeria.gestion_envio.exception.BusinessConflictException;
import com.joyeria.gestion_envio.exception.ExternalServiceException;
import com.joyeria.gestion_envio.exception.ResourceNotFoundException;
import com.joyeria.gestion_envio.model.Historial;
import com.joyeria.gestion_envio.repository.EnvioRepository;
import com.joyeria.gestion_envio.repository.HistorialRepository;

import feign.FeignException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HistorialService {

    private final HistorialRepository historialRep;
    private final EnvioRepository envioRep;
    private final UsuarioClient usuarioClient;

    // Método privado para mapear de Entidad a DTO
    private HistorialResponseDTO convertToDto(Historial h) {
        if (h == null) return null;
        return new HistorialResponseDTO(
                h.getIdHistorial(),
                h.getIdEnvio(),
                h.getFecha(),
                h.getEstado(),
                h.getIdUsuario()
        );
    }

    @Transactional(readOnly = true)
    public List<HistorialResponseDTO> getAllHistorial() {
        log.info("[HistorialService] Consultando todo el historial de movimientos");
        return historialRep.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistorialResponseDTO> getHistorialByEnvioId(Long envioId) {
        log.info("[HistorialService] Buscando historial completo para el Envío ID: {}", envioId);
        if (!envioRep.existsById(envioId)) {
            log.warn("[HistorialService] El envío ID {} no existe en la base de datos", envioId);
            throw new ResourceNotFoundException("No se encontró el envío con ID: " + envioId);
        }
        return historialRep.findByIdEnvio(envioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HistorialResponseDTO findByIdOrThrow(Long id) {
        log.info("[HistorialService] Buscando registro de historial con ID: {}", id);
        Historial h = historialRep.findById(id)
                .orElseThrow(() -> {
                    log.warn("[HistorialService] Registro de historial ID {} no encontrado", id);
                    return new ResourceNotFoundException("No se encontró el registro de historial con ID: " + id);
                });
        return convertToDto(h);
    }

    @Transactional
    public HistorialResponseDTO saveHistorial(HistorialRequestDTO request) {
        log.info("[HistorialService] Iniciando registro de nuevo movimiento para el Envío ID: {}", request.getIdEnvio());
        if (request.getIdEnvio() == null || request.getEstado() == null || request.getEstado().isBlank() || request.getIdUsuario() == null) {
            log.warn("[HistorialService] Fallo al crear historial: Faltan datos obligatorios");
            throw new BusinessConflictException("El ID de envío, el estado y el ID de usuario son obligatorios.");
        }

        if (!envioRep.existsById(request.getIdEnvio())) {
            log.warn("[HistorialService] Fallo al crear historial: Envío ID {} no existe", request.getIdEnvio());
            throw new ResourceNotFoundException("El envío con ID " + request.getIdEnvio() + " no existe.");
        }

        try {
            usuarioClient.obtenerUsuarioPorId(request.getIdUsuario());
        } catch (FeignException.NotFound e) {
            log.warn("[HistorialService] Usuario ID {} no encontrado en ms-usuarios", request.getIdUsuario());
            throw new ResourceNotFoundException("No se puede registrar el historial: El usuario con ID " + request.getIdUsuario() + " no existe.");
        } catch (FeignException e) {
            log.error("[HistorialService] Error de comunicación con ms-usuarios: {}", e.getMessage());
            throw new ExternalServiceException("El microservicio de usuarios no se encuentra disponible en este momento.");
        }

        Historial historial = new Historial();
        historial.setIdEnvio(request.getIdEnvio());
        historial.setEstado(request.getEstado());
        historial.setIdUsuario(request.getIdUsuario());
        historial.setFecha(LocalDateTime.now());

        Historial guardado = historialRep.save(historial);
        log.info("[HistorialService] Movimiento de historial ID {} registrado exitosamente", guardado.getIdHistorial());
        return convertToDto(guardado);
    }
}