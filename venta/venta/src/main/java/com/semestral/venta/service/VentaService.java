package com.semestral.venta.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.semestral.venta.dto.VentaRequestDTO;
import com.semestral.venta.dto.VentaResponseDTO;
import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.DetalleRepository;
import com.semestral.venta.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRe;
    private final DetalleRepository detalleRe;


    public VentaResponseDTO registrarVenta(VentaRequestDTO re){
        Venta ventaNueva = new Venta();
        ventaNueva.setFechaV(LocalDateTime.now());
        ventaNueva.setTotal(0.0); //se sumaran los subtotales

        Venta guardarVenta = ventaRe.save(ventaNueva);

        Double total = 0.0;
        List<Detalle> listaDetalles = new ArrayList<>();

        


    }

    


}
