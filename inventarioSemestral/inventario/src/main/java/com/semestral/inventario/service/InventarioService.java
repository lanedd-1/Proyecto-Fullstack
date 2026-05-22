package com.semestral.inventario.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Inventario;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;
import com.semestral.inventario.repository.EstanteRepository;
import com.semestral.inventario.repository.InventarioStockRepository;
import com.semestral.inventario.repository.PasilloRepository;
import com.semestral.inventario.repository.UbicacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {


    private final InventarioStockRepository inventarioRe;
    private final UbicacionRepository ubicacionRe;
    private final PasilloRepository pasillRe;
    private final EstanteRepository estanteRe;

    public InventarioResponseDTO agregarStock(InventarioRequestDTO re){


        Pasillo pasillo = pasillRe.findById(re.getIdPasillo())
            .orElseThrow(() -> new NoSuchElementException("Pasillo no encontrado"));
        Estante estante = estanteRe.findById(re.getIdEstante())
            .orElseThrow(() -> new NoSuchElementException("Estante no encontrado"));
        Ubicacion ubicacion = ubicacionRe.findByPasilloIdAndEstanteId(
            re.getIdPasillo(), re.getIdEstante())
            .orElseThrow(() -> new NoSuchElementException(
                "Error: La combinación de Pasillo " + pasillo.getNombrePasillo() + 
                " y Estante " + estante.getNombreEstante() + " no está habilitada en el sistema."));

        Optional<Inventario> stockExistente = inventarioRe.findByIdProductoAndUbicacionId(re.getIdProducto(), ubicacion.getIdPasEst());

        Inventario registroStock;

        if (stockExistente.isPresent()) {
            registroStock = stockExistente.get();
            registroStock.setStock(registroStock.getStock() + re.getCantidad());
        } else {
            registroStock = new Inventario(null, re.getCantidad(), re.getIdProducto(), ubicacion);
        }

        Inventario guardar = inventarioRe.save(registroStock);
        return convertToDTO(guardar);
    }


    public InventarioResponseDTO descontarStock(InventarioRequestDTO re){
        Ubicacion ubi = ubicacionRe
        .findByPasilloIdAndEstanteId(re.getIdPasillo(), re.getIdEstante())
        .orElseThrow(() -> new NoSuchElementException("No se encontro la ubicacion del Objeto con el id" + re.getIdPasillo() + re.getIdEstante() ));

        Inventario registro = inventarioRe
        .findByIdProductoAndUbicacionId(re.getIdProducto(), ubi.getIdPasEst())
        .orElseThrow(() -> new NoSuchElementException("El producto no registra stock en esta ubicacion"));


        if (registro.getStock() < re.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente en esta ubicación. Disponible: " 
                    + registro.getStock() + ", Solicitado: " + re.getCantidad());
    }

    registro.setStock(registro.getStock() - re.getCantidad());
        Inventario actualizado = inventarioRe.save(registro);
        return convertToDTO(actualizado);
}


public List<InventarioResponseDTO> getStockPorProducto (Long idPps){
    return inventarioRe.findByIdProducto(idPps).stream()
    .map(this::convertToDTO).collect(Collectors.toList());
}

public List<InventarioResponseDTO> getTodoStock(){
return inventarioRe.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());

}


    private InventarioResponseDTO convertToDTO(Inventario s) {
        return new InventarioResponseDTO(
            s.getIdInv(),
            s.getIdProd(),
            s.getIdPasEstante().getIdPasillo().getNombrePasillo(),
            s.getIdPasEstante().getIdEstante().getNombreEstante(),
            s.getStock()
        );
    }
}
