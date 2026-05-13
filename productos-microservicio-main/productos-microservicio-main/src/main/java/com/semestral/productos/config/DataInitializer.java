package com.semestral.productos.config;
//parametros de inicio

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.semestral.productos.model.Categoria;
import com.semestral.productos.model.Productos;
import com.semestral.productos.repository.CategoriaRepository;
import com.semestral.productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

    private final ProductoRepository ProdRepo;
    private final CategoriaRepository catRepo;

    @Override
    //determinar si la tabla presenta datos ya cargados
    public void run(String... args){
        Categoria categoria = catRepo.findById(1L)
                .orElseGet(() -> catRepo.save(new Categoria(null, "Pulseras")));

        if (ProdRepo.count() > 0) {
            log.info("Tabla Productos con datos precargados, se omite la carga inicial");
            return;
        }

        //sino se crean datos para manipular la BD
        ProdRepo.save(new Productos(null,
            "LMiun122",
            "Pulsera de plata",
            "pulsera de plata 9/25 con insignia de oro de 14K (58,5% oro)",
            new BigDecimal(85990),
            "https://images.pexels.com/photos/16304608/pexels-photo-16304608.jpeg",
            25L, 
            categoria));

        ProdRepo.save(new Productos(
            null,
            "LMiun12",
            "Pulsera de cobre",
            "pulsera de plata 9/25 con insignia de oro de 14K (58,5% oro)",
            new BigDecimal(85990), 
            "https://images.pexels.com/photos/16304608/pexels-photo-16304608.jpeg",
            25L,
            categoria));

        ProdRepo.save(new Productos(
        null,
        "LMiun122",
        "Pulsera de plata",
        "pulsera de plata 9/25 con insignia de oro de 14K",
        new BigDecimal(85990),
        "https://images.pexels.com/photos/16304608/pexels-photo-16304608.jpeg",
        25L, 
        categoria));    
    }









}
