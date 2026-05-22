package com.joyeria.gestion_envio.service;
import com.joyeria.gestion_envio.client.UsuarioClient;
import com.joyeria.gestion_envio.dto.HistorialRequestDTO;
import com.joyeria.gestion_envio.dto.HistorialResponseDTO;
import com.joyeria.gestion_envio.exception.BusinessConflictException;
import com.joyeria.gestion_envio.exception.ExternalServiceException;
import com.joyeria.gestion_envio.exception.ResourceNotFoundException;
import com.joyeria.gestion_envio.model.Historial;
import com.joyeria.gestion_envio.repository.HistorialRepository;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HistorialService {

    private final HistorialRepository historialRep;
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

    @Transactional
    public List<HistorialResponseDTO> getAllHistorial() {
        return historialRep.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<HistorialResponseDTO> getHistorialByEnvioId(Long envioId) {
        return historialRep.findByIdEnvio(envioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public HistorialResponseDTO findByIdOrThrow(Long id) {
        Historial h = historialRep.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el registro de historial con ID: " + id));
        return convertToDto(h);
    }

    @Transactional
    public HistorialResponseDTO saveHistorial(HistorialRequestDTO request) {
        // 1. Validaciones iniciales
        if (request.getIdEnvio() == null || request.getEstado() == null || request.getEstado().isBlank() || request.getIdUsuario() == null) {
            throw new BusinessConflictException("El ID de envío, el estado y el ID de usuario son obligatorios.");
        }
        try {
            usuarioClient.obtenerUsuarioPorId(request.getIdUsuario());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("No se puede registrar el historial: El usuario con ID " + request.getIdUsuario() + " no existe.");
        } catch (FeignException e) {
            throw new ExternalServiceException("Error de comunicación con el servicio de usuarios. Intente más tarde.");
        }

        Historial historial = new Historial();
        historial.setIdEnvio(request.getIdEnvio());
        historial.setEstado(request.getEstado());
        historial.setIdUsuario(request.getIdUsuario());
        historial.setFecha(LocalDateTime.now());

        Historial guardado = historialRep.save(historial);
        return convertToDto(guardado);
    }
}
