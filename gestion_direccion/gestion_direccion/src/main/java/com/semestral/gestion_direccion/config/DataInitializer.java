package com.semestral.gestion_direccion.config;

import com.semestral.gestion_direccion.model.Comuna;
import com.semestral.gestion_direccion.model.Direccion;
import com.semestral.gestion_direccion.model.Region;
import com.semestral.gestion_direccion.repository.ComunaRepository;
import com.semestral.gestion_direccion.repository.DireccionRepository;
import com.semestral.gestion_direccion.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final DireccionRepository direccionRepository;

    @Override
    public void run(String... args) {
        // Verificamos si ya hay datos para no duplicar
        if (direccionRepository.count() > 0) {
            log.info("Tablas con datos precargados, se omite carga inicial");
            return;
        }
        // 1. Crear Región
        Region rm = new Region();
        rm.setNombre_region("Metropolitana");
        regionRepository.save(rm);


        // 3. Crear Comuna (vinculada a la Región)
        Comuna santiago = new Comuna();
        santiago.setNombreC("Santiago");
        santiago.setRegion(rm);
        comunaRepository.save(santiago);

        Comuna huechuraba = new Comuna();
        huechuraba.setNombreC("Huechuraba");
        huechuraba.setRegion(rm);
        comunaRepository.save(huechuraba);

        Comuna quilicura = new Comuna();
        quilicura.setNombreC("Quilicura");
        quilicura.setRegion(rm);
        comunaRepository.save(quilicura);
        
        Comuna conchali=new Comuna();
        conchali.setNombreC("Conchali");
        conchali.setRegion(rm);
        comunaRepository.save(conchali);

        direccionRepository.save(new Direccion(null,"Avenida Siempre Viva", "123", santiago));
        log.info("Tabla Direccion: {} Datos Cargados",direccionRepository.count());
    }
}