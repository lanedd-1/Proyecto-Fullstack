package com.joyeria.gestionestado.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.joyeria.gestionestado.model.Estado;
import com.joyeria.gestionestado.repository.EstadoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

      private final EstadoRepository estadoRepository;

    @Override
    public void run (String... args){

        if(estadoRepository.count() > 0 ){
            log.info("La tabla 'estado' ya tiene datos, se omite la carga inicial");
            return;
        }

        log.info("DataInitializer: cargando estados iniciales...");

        estadoRepository.save(new Estado(null, "Activo"));
        estadoRepository.save(new Estado(null, "Inactivo"));
        estadoRepository.save(new Estado(null, "Pendiente"));
        estadoRepository.save(new Estado(null, "En camino"));
        estadoRepository.save(new Estado(null, "Entregado"));
        estadoRepository.save(new Estado(null, "Cancelado"));

        log.info("DataInitializer: {} estados cargados correctamente.", estadoRepository.count());

    }

}
