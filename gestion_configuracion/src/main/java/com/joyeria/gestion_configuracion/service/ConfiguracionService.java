package com.joyeria.gestion_configuracion.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyeria.gestion_configuracion.client.UsuarioClient;
import com.joyeria.gestion_configuracion.dto.ConfiguracionRequestDTO;
import com.joyeria.gestion_configuracion.dto.ConfiguracionResponseDTO;
import com.joyeria.gestion_configuracion.dto.UsuarioResponseDTO;
import com.joyeria.gestion_configuracion.exception.ConfiguracionLongitudInvalidaException;
import com.joyeria.gestion_configuracion.exception.ConfiguracionNotFoundException;
import com.joyeria.gestion_configuracion.model.Configuracion;
import com.joyeria.gestion_configuracion.repository.ConfiguracionRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracionService {
    private final ConfiguracionRepository configuracionRepository;
    private final UsuarioClient usuarioClient;

    private static final Long ID_UNICO = 1L;

    @Transactional(readOnly = true)
    public ConfiguracionResponseDTO getConfiguracion() {
        log.info("Consultando configuracion de contrasena");

        Configuracion config = configuracionRepository.findById(ID_UNICO)
                .orElseThrow(() -> {
                    log.warn("No existe configuracion en la BD");
                    return new ConfiguracionNotFoundException();
                });

        List<UsuarioResponseDTO> usuarios = obtenerUsuariosDesdeMs();
        return toResponse(config, usuarios);
    }

    @Transactional
    public ConfiguracionResponseDTO update(ConfiguracionRequestDTO req) {
        log.info("Actualizando configuracion de contrasena");

        if (req.getLongitudMinima() >= req.getLongitudMaxima()) {
            throw new ConfiguracionLongitudInvalidaException(
                    req.getLongitudMinima(), req.getLongitudMaxima());
        }

        Configuracion existente = configuracionRepository.findById(ID_UNICO)
                .orElseThrow(() -> {
                    log.warn("No existe configuracion en la BD");
                    return new ConfiguracionNotFoundException();
                });

        existente.setLongitudMinima(req.getLongitudMinima());
        existente.setLongitudMaxima(req.getLongitudMaxima());
        existente.setRequiereMayuscula(req.getRequiereMayuscula());
        existente.setRequiereMinuscula(req.getRequiereMinuscula());
        existente.setRequiereNumero(req.getRequiereNumero());
        existente.setRequiereCaracterEspecial(req.getRequiereCaracterEspecial());
        existente.setCaracteresEspecialesPermitidos(req.getCaracteresEspecialesPermitidos());

        Configuracion actualizada = configuracionRepository.save(existente);
        log.info("Configuracion actualizada correctamente");
        return toResponse(actualizada, null);
    }


    private List<UsuarioResponseDTO> obtenerUsuariosDesdeMs() {
        try {
            log.debug("Llamando a ms-usuario via Feign");
            List<UsuarioResponseDTO> usuarios = usuarioClient.getAllUsuarios();
            log.debug("ms-usuario retorno {} usuarios", usuarios.size());
            return usuarios;
        } catch (FeignException ex) {
            log.warn("ms-usuario no disponible | Status: {} | Se retorna lista vacia", ex.status());
            return Collections.emptyList();
        }
    }

    private ConfiguracionResponseDTO toResponse(Configuracion c, List<UsuarioResponseDTO> usuarios) {
        return new ConfiguracionResponseDTO(
                c.getLongitudMinima(), c.getLongitudMaxima(),
                c.getRequiereMayuscula(), c.getRequiereMinuscula(),
                c.getRequiereNumero(), c.getRequiereCaracterEspecial(),
                c.getCaracteresEspecialesPermitidos(),
                usuarios
        );
    }
}
