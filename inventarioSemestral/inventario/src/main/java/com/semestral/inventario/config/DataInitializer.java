package com.semestral.inventario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Inventario;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;
import com.semestral.inventario.repository.EstanteRepository;
import com.semestral.inventario.repository.InventarioStockRepository;
import com.semestral.inventario.repository.PasilloRepository;
import com.semestral.inventario.repository.UbicacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

    private final EstanteRepository estRe;
    private final InventarioStockRepository invRe;
    private final PasilloRepository pasRe;
    private final UbicacionRepository ubiRe;


    @Override

    public void run(String... args){
        Estante est = estRe.findById(1L)
        .orElseGet(() -> estRe.save(new Estante(null, "estante 1")));

        Pasillo pas = pasRe.findById(1L)
        .orElseGet(() -> pasRe.save(new Pasillo(null, "pasillo 1")));

        Ubicacion ubi = ubiRe.findByPasilloIdAndEstanteId(pas.getIdPasillo(), est.getIdEstante())
        .orElseGet(() -> ubiRe.save(new Ubicacion(null, pas, est)));

        if (invRe.count() > 0) {
            log.info("Tabla Inventario con datos precargados, se omite la carga inicial");
            return;
        }


        invRe.save(new Inventario(
            null,
            25,
            1L,
            ubi
        ));
    }

}
