package com.semestral.gestion_direccion.service;

import com.semestral.gestion_direccion.client.EstadoClient;
import com.semestral.gestion_direccion.client.UsuarioClient;
import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.exception.ResourceNotFoundException;
import com.semestral.gestion_direccion.model.Comuna;
import com.semestral.gestion_direccion.model.Direccion;
import com.semestral.gestion_direccion.model.Region;
import com.semestral.gestion_direccion.repository.ComunaRepository;
import com.semestral.gestion_direccion.repository.DireccionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DireccionService {
    private final DireccionRepository direccionRep;
    private final ComunaRepository comunaRep;
    private final UsuarioClient usuarioClient;
    private final EstadoClient estadoClient;

    @Transactional(readOnly = true)
    public List<DireccionResponseDTO> findAll() {
        log.info("[DireccionService] Consultando todas las direcciones (con JOIN FETCH de Comuna y Región)");
        List<Direccion> list = direccionRep.findAllWithComunaAndRegion();
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DireccionResponseDTO findByIdOrThrow(Long id) {
        log.info("[DireccionService] Buscando dirección con ID: {}", id);
        Direccion d = direccionRep.findByIdWithComunaAndRegion(id)
                .orElseThrow(() -> {
                    log.warn("[DireccionService] Dirección ID {} no encontrada", id);
                    return new ResourceNotFoundException(id);
                });
        return toResponse(d);
    }

    @Transactional
    public DireccionResponseDTO create(DireccionRequestDTO req) {
        log.info("[DireccionService] Iniciando creación de dirección para el usuario ID: {}", req.getIdUsuario());

        if (req.getIdComuna() == null) {
            log.warn("[DireccionService] Fallo al crear: El ID de la comuna es nulo");
            throw new RuntimeException("El id de comuna es obligatorio");
        }
        
        if (req.getIdUsuario() != null) {
            try {
                usuarioClient.obtenerUsuarioPorId(req.getIdUsuario());
            } catch (FeignException.NotFound e) {
                log.warn("[DireccionService] Validación fallida: Usuario ID {} no existe en ms-usuarios", req.getIdUsuario());
                throw new ResourceNotFoundException(req.getIdUsuario());
            } catch (FeignException e) {
                log.error("[DireccionService] Error de comunicación con ms-usuarios al validar ID {}: {}", req.getIdUsuario(), e.getMessage());
                throw new RuntimeException("Error de comunicación con el servicio de Usuarios.");
            }
        }
        
        if (req.getIdEstado() != null) {
            try {
                estadoClient.obtenerEstadoPorId(req.getIdEstado());
            } catch (FeignException.NotFound e) {
                log.warn("[DireccionService] Validación fallida: Estado ID {} no existe en ms-estados", req.getIdEstado());
                throw new ResourceNotFoundException(req.getIdEstado());
            } catch (FeignException e) {
                log.error("[DireccionService] Error de comunicación con ms-estados al validar ID {}: {}", req.getIdEstado(), e.getMessage());
                throw new RuntimeException("Error de comunicación con el servicio de Estados.");
            }
        }

        Comuna comuna = comunaRep.findById(req.getIdComuna())
                .orElseThrow(() -> {
                    log.warn("[DireccionService] Comuna ID {} no encontrada en la BD", req.getIdComuna());
                    return new ResourceNotFoundException(req.getIdComuna());
                });

        Direccion d = new Direccion();
        d.setIdDireccion(null);
        d.setCalle(req.getCalle());
        d.setNumero(req.getNumero());
        d.setComuna(comuna);
        d.setIdUsuario(req.getIdUsuario());
        d.setIdEstado(req.getIdEstado());

        Direccion saved = direccionRep.save(d);
        log.info("[DireccionService] Dirección creada exitosamente con ID: {}", saved.getIdDireccion());
        return toResponse(saved);
    }

    @Transactional
    public DireccionResponseDTO update(Long id, DireccionRequestDTO req) {
        log.info("[DireccionService] Iniciando actualización de la dirección ID: {}", id);
        
        Direccion existing = direccionRep.findById(id)
                .orElseThrow(() -> {
                    log.warn("[DireccionService] Fallo al actualizar: Dirección ID {} no encontrada", id);
                    return new ResourceNotFoundException(id);
                });

        if (req.getCalle() != null) existing.setCalle(req.getCalle());
        if (req.getNumero() != null) existing.setNumero(req.getNumero());

        if (req.getIdComuna() != null) {
            Comuna comuna = comunaRep.findById(req.getIdComuna())
                    .orElseThrow(() -> {
                        log.warn("[DireccionService] Fallo al actualizar: Comuna ID {} no encontrada", req.getIdComuna());
                        return new ResourceNotFoundException(req.getIdComuna());
                    });
            existing.setComuna(comuna);
        }

        if (req.getIdUsuario() != null) {
            try {
                usuarioClient.obtenerUsuarioPorId(req.getIdUsuario());
            } catch (FeignException.NotFound e) {
                log.warn("[DireccionService] Fallo al actualizar: Usuario ID {} no existe en ms-usuarios", req.getIdUsuario());
                throw new ResourceNotFoundException(req.getIdUsuario());
            } catch (FeignException e) {
                log.error("[DireccionService] Error de red con ms-usuarios: {}", e.getMessage());
                throw new RuntimeException("Error de comunicación con el servicio de Usuarios.");
            }
            existing.setIdUsuario(req.getIdUsuario());
        }
        
        if (req.getIdEstado() != null) {
            try {
                estadoClient.obtenerEstadoPorId(req.getIdEstado());
            } catch (FeignException.NotFound e) {
                log.warn("[DireccionService] Fallo al actualizar: Estado ID {} no existe en ms-estados", req.getIdEstado());
                throw new ResourceNotFoundException(req.getIdEstado());
            } catch (FeignException e) {
                log.error("[DireccionService] Error de red con ms-estados: {}", e.getMessage());
                throw new RuntimeException("Error de comunicación con el servicio de Estados.");
            }
            existing.setIdEstado(req.getIdEstado());
        }

        Direccion saved = direccionRep.save(existing);
        log.info("[DireccionService] Dirección ID {} actualizada correctamente", saved.getIdDireccion());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        log.info("[DireccionService] Intentando eliminar la dirección ID: {}", id);
        if (!direccionRep.existsById(id)) {
            log.warn("[DireccionService] Intento de eliminar ID inexistente: {}", id);
            throw new ResourceNotFoundException(id);
        }
        direccionRep.deleteById(id);
        log.info("[DireccionService] Dirección ID {} eliminada correctamente", id);
    }

    private DireccionResponseDTO toResponse(Direccion d) {
        if (d == null) return null;
        String nombreComuna = null;
        String nombreRegion = null;

        Comuna c = d.getComuna();
        if (c != null) {
            nombreComuna = c.getNombreC();
            Region r = c.getRegion();
            if (r != null) nombreRegion = r.getNombreRegion();
        }

        return new DireccionResponseDTO(
                d.getIdDireccion(),
                d.getCalle(),
                d.getNumero(),
                nombreComuna,
                nombreRegion,
                d.getIdUsuario(),  
                d.getIdEstado()    
        );
    }
}