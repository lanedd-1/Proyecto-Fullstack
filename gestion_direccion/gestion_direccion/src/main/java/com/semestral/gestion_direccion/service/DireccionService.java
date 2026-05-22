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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DireccionService {
    private final DireccionRepository direccionRep;
    private final ComunaRepository comunaRep;
    private final UsuarioClient usuarioClient;
    private final EstadoClient estadoClient;

    @Transactional(readOnly = true)
    public List<DireccionResponseDTO> findAll() {
        List<Direccion> list = direccionRep.findAllWithComunaAndRegion();
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DireccionResponseDTO findByIdOrThrow(Long id) {
        Direccion d = direccionRep.findByIdWithComunaAndRegion(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return toResponse(d);
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDTO> findByIds(Collection<Long> ids) {
        List<Direccion> list = direccionRep.findAllById(ids);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public DireccionResponseDTO create(DireccionRequestDTO req) {
        if (req.getIdComuna() == null) {
            throw new RuntimeException("El id de comuna es obligatorio");
        }
        
        if (req.getIdUsuario() != null) {
            try {
                usuarioClient.obtenerUsuarioPorId(req.getIdUsuario());
            } catch (FeignException.NotFound e) {
                throw new ResourceNotFoundException(req.getIdUsuario());
            } catch (FeignException e) {
                throw new RuntimeException("Error de comunicación con el servicio de Usuarios.");
            }
        }
            /* 

        if (req.getIdEstado() != null) {
            try {
                estadoClient.obtenerEstadoPorId(req.getIdEstado());
            } catch (FeignException.NotFound e) {
                throw new ResourceNotFoundException(req.getIdEstado());
            } catch (FeignException e) {
                throw new RuntimeException("Error de comunicación con el servicio de Estados.");
            }
        }
        */
        // ----------------------------------------------

        Comuna comuna = comunaRep.findById(req.getIdComuna())
                .orElseThrow(() -> new ResourceNotFoundException(req.getIdComuna()));

        Direccion d = new Direccion();
        d.setIdDireccion(null);
        d.setCalle(req.getCalle());
        d.setNumero(req.getNumero());
        d.setComuna(comuna);
        
        d.setIdUsuario(req.getIdUsuario());
        d.setIdEstado(req.getIdEstado());

        Direccion saved = direccionRep.save(d);
        return toResponse(saved);
    }

    @Transactional
    public DireccionResponseDTO update(Long id, DireccionRequestDTO req) {
        Direccion existing = direccionRep.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (req.getCalle() != null) existing.setCalle(req.getCalle());
        if (req.getNumero() != null) existing.setNumero(req.getNumero());

        if (req.getIdComuna() != null) {
            Comuna comuna = comunaRep.findById(req.getIdComuna())
                    .orElseThrow(() -> new ResourceNotFoundException(req.getIdComuna()));
            existing.setComuna(comuna);
        }

        if (req.getIdUsuario() != null) {
            try {
                usuarioClient.obtenerUsuarioPorId(req.getIdUsuario());
            } catch (FeignException.NotFound e) {
                throw new ResourceNotFoundException(req.getIdUsuario());
            } catch (FeignException e) {
                throw new RuntimeException("Error de comunicación con el servicio de Usuarios.");
            }
            existing.setIdUsuario(req.getIdUsuario());
        }
        /* 

        if (req.getIdEstado() != null) {
            try {
                estadoClient.obtenerEstadoPorId(req.getIdEstado());
            } catch (FeignException.NotFound e) {
                throw new ResourceNotFoundException(req.getIdEstado());
            } catch (FeignException e) {
                throw new RuntimeException("Error de comunicación con el servicio de Estados.");
            }
            existing.setIdEstado(req.getIdEstado());
        }
        */
        // ----------------------------------------------------------
        
        // Si tienes el bypass activo, igual necesitamos actualizar los campos en la entidad:
        if (req.getIdUsuario() != null) existing.setIdUsuario(req.getIdUsuario());
        if (req.getIdEstado() != null) existing.setIdEstado(req.getIdEstado());

        Direccion saved = direccionRep.save(existing);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!direccionRep.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        direccionRep.deleteById(id);
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

        // Agregamos idUsuario e idEstado a la respuesta
        return new DireccionResponseDTO(
                d.getIdDireccion(),
                d.getCalle(),
                d.getNumero(),
                nombreComuna,
                nombreRegion,
                d.getIdUsuario(),  // <--- Revisa que tu DTO acepte estos parámetros
                d.getIdEstado()    // <--- Revisa que tu DTO acepte estos parámetros
        );
    }
}