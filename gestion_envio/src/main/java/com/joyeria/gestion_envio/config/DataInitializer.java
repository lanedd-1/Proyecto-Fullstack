package com.joyeria.gestion_envio.config;

import com.joyeria.gestion_envio.model.Envio;
import com.joyeria.gestion_envio.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EnvioRepository envioRep;

    @Override
    public void run(String... args) {
        if (envioRep.count() > 0) {
            log.info("Tabla Envios con datos precargados, se omite la carga inicial");
            return;
        }

        Envio envio1 = new Envio();
        envio1.setIdVenta(1L);
        envio1.setIdDireccion(1L);
        envio1.setEstado("PREPARACION");
        
        envio1.setFechaEnvio(LocalDateTime.now()); 
        envio1.setFechaRecep(LocalDateTime.now().plusDays(3));
        envioRep.save(envio1);
        
        log.info("Envío de prueba creado exitosamente con Venta ID 1 y Dirección ID 1.");
    }
}