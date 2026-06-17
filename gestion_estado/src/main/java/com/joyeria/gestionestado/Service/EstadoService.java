package com.joyeria.gestionestado.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyeria.gestionestado.client.EnvioClient;
import com.joyeria.gestionestado.dto.EnvioResponseDTO;
import com.joyeria.gestionestado.dto.EstadoConEnviosResponseDTO;
import com.joyeria.gestionestado.dto.EstadoRequestDTO;
import com.joyeria.gestionestado.dto.EstadoResponseDTO;
import com.joyeria.gestionestado.exception.EstadoDuplicadoException;
import com.joyeria.gestionestado.exception.EstadoNotFoundException;
import com.joyeria.gestionestado.model.Estado;
import com.joyeria.gestionestado.repository.EstadoRepository;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EstadoService {
    private final EstadoRepository estadoRepository;
    private final EnvioClient envioClient;

    public EstadoService(EstadoRepository estadoRepository, EnvioClient envioClient) {
        this.estadoRepository = estadoRepository;
        this.envioClient = envioClient;
    }


    @Transactional(readOnly = true)
    public List<EstadoResponseDTO> obtenerTodos() {
        log.info("Consultando lista completa de estados");
        return estadoRepository.findAll().stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstadoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando estado con ID: {}", id);
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado con ID {} no existe", id);
                    return new EstadoNotFoundException(id);
                });
        return mapearAResponseDTO(estado);
    }

   
    @Transactional(readOnly = true)
    public EstadoConEnviosResponseDTO obtenerConEnvios(Long id) {
        log.info("Buscando estado ID: {} con sus envíos", id);

        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado con ID {} no existe", id);
                    return new EstadoNotFoundException(id);
                });

        List<EnvioResponseDTO> envios = obtenerEnviosDesdeMs(estado.getNombreEstado());

        log.info("Estado '{}' tiene {} envíos asociados",
                estado.getNombreEstado(), envios.size());

        return new EstadoConEnviosResponseDTO(
                estado.getIdEstado(),
                estado.getNombreEstado(),
                envios
        );
    }

    @Transactional
    public EstadoResponseDTO saveEstado(EstadoRequestDTO dto) {
        log.info("Creando estado: '{}'", dto.getNombreEstado());

        if (estadoRepository.existsByNombreEstadoIgnoreCase(dto.getNombreEstado())) {
            log.warn("Estado duplicado: '{}'", dto.getNombreEstado());
            throw new EstadoDuplicadoException(dto.getNombreEstado());
        }

        Estado nuevo = new Estado();
        nuevo.setNombreEstado(dto.getNombreEstado());
        Estado guardado = estadoRepository.save(nuevo);

        log.info("Estado creado. ID: {}", guardado.getIdEstado());
        return mapearAResponseDTO(guardado);
    }

    @Transactional
    public EstadoResponseDTO update(Long id, EstadoRequestDTO dto) {
        log.info("Actualizando estado ID: {}", id);

        Estado existente = estadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estado inexistente ID: {}", id);
                    return new EstadoNotFoundException(id);
                });

        estadoRepository.findByNombreEstadoIgnoreCase(dto.getNombreEstado())
                .ifPresent(otro -> {
                    if (!otro.getIdEstado().equals(id)) {
                        throw new EstadoDuplicadoException(dto.getNombreEstado());
                    }
                });

        existente.setNombreEstado(dto.getNombreEstado());
        Estado actualizado = estadoRepository.save(existente);

        log.info("Estado ID {} actualizado a '{}'", id, actualizado.getNombreEstado());
        return mapearAResponseDTO(actualizado);
    }



    private List<EnvioResponseDTO> obtenerEnviosDesdeMs(String nombreEstado) {
        try {
            log.debug("Llamando a ms-envio via Feign para estado: '{}'", nombreEstado);
            List<EnvioResponseDTO> todos = envioClient.obtenerTodos();
            List<EnvioResponseDTO> filtrados = todos.stream()
                    .filter(e -> nombreEstado.equalsIgnoreCase(e.getEstado()))
                    .collect(Collectors.toList());
            log.debug("ms-envio retornó {} envíos para estado '{}'", filtrados.size(), nombreEstado);
            return filtrados;
        } catch (FeignException ex) {
            log.warn("ms-envio no disponible | Status: {} | Se retorna lista vacía", ex.status());
            return Collections.emptyList();
        }
    }

    private EstadoResponseDTO mapearAResponseDTO(Estado e) {
        return new EstadoResponseDTO(e.getIdEstado(), e.getNombreEstado());
    }

}
