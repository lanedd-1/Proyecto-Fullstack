package com.joyeria.gestion_configuracion.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.joyeria.gestion_configuracion.model.Configuracion;
import com.joyeria.gestion_configuracion.repository.ConfiguracionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

    private final ConfiguracionRepository configuracionRepository;

    @Override
    public void run(String... args) {
        if (configuracionRepository.count() > 0) {
            log.info("DataInitializer: configuracion ya existe, se omite la carga inicial.");
            return;
        }

        configuracionRepository.save(new Configuracion(
                1L,         // id fijo, siempre sera 1
                8,          // longitud minima
                20,         // longitud maxima
                true,       // requiere mayuscula
                true,       // requiere minuscula
                true,       // requiere numero
                true,       // requiere caracter especial
                "!@#$%&*"   // caracteres especiales permitidos
        ));

        log.info("DataInitializer: configuracion inicial cargada.");
    }

}
