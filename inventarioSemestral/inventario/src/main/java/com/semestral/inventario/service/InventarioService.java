package com.semestral.inventario.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.inventario.client.ProductosClient;
import com.semestral.inventario.dto.EstanteRequestDTO;
import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.dto.PasilloRequestDTO;
import com.semestral.inventario.dto.UbicacionRequestDTO;
import com.semestral.inventario.model.Estante;
import com.semestral.inventario.model.Inventario;
import com.semestral.inventario.model.Pasillo;
import com.semestral.inventario.model.Ubicacion;
import com.semestral.inventario.repository.EstanteRepository;
import com.semestral.inventario.repository.InventarioStockRepository;
import com.semestral.inventario.repository.PasilloRepository;
import com.semestral.inventario.repository.UbicacionRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {


    private final InventarioStockRepository inventarioRe;
    private final UbicacionRepository ubicacionRe;
    private final PasilloRepository pasillRe;
    private final EstanteRepository estanteRe;
    private final ProductosClient prodCli;

    public InventarioResponseDTO agregarStock(InventarioRequestDTO re){

        Ubicacion ubicacion = findOrCreateUbicacion(re.getIdPasillo(), re.getIdEstante());

        Optional<Inventario> stockExistente = inventarioRe.findByIdProductoAndUbicacionId(validarProd(re.getIdProd()), ubicacion.getIdPasEst());

        Inventario registroStock;

        if (stockExistente.isPresent()) {
            registroStock = stockExistente.get();
            registroStock.setStock(registroStock.getStock() + re.getCantidad());
        } else {
            registroStock = new Inventario(null, re.getCantidad(), re.getIdProd(), ubicacion);
        }

        Inventario guardar = inventarioRe.save(registroStock);
        return convertToDTO(guardar);
    }

    public Pasillo crearPasillo(PasilloRequestDTO request) {
        return pasillRe.save(new Pasillo(null, request.getNombrePasillo()));
    }

    public Estante crearEstante(EstanteRequestDTO request) {
        return estanteRe.save(new Estante(null, request.getNombreEstante()));
    }

    public Ubicacion crearUbicacion(UbicacionRequestDTO request) {
        Pasillo pasillo = pasillRe.findById(request.getIdPasillo())
            .orElseThrow(() -> new NoSuchElementException("Pasillo no encontrado"));
        Estante estante = estanteRe.findById(request.getIdEstante())
            .orElseThrow(() -> new NoSuchElementException("Estante no encontrado"));

        return ubicacionRe.findByPasilloIdAndEstanteId(pasillo.getIdPasillo(), estante.getIdEstante())
            .orElseGet(() -> ubicacionRe.save(new Ubicacion(null, pasillo, estante)));
    }

    private Ubicacion findOrCreateUbicacion(Long idPasillo, Long idEstante) {
        Pasillo pasillo = pasillRe.findById(idPasillo)
            .orElseThrow(() -> new NoSuchElementException("Pasillo no encontrado"));
        Estante estante = estanteRe.findById(idEstante)
            .orElseThrow(() -> new NoSuchElementException("Estante no encontrado"));

        return ubicacionRe.findByPasilloIdAndEstanteId(idPasillo, idEstante)
            .orElseGet(() -> ubicacionRe.save(new Ubicacion(null, pasillo, estante)));
    }

    public Long validarProd(Long idProd){
        try {
            prodCli.obtenerId(idProd);
            return idProd;
            
        } catch (FeignException.NotFound ex) {
            throw new RuntimeException("El producto no existe");
        } catch (FeignException e){
            throw new RuntimeException("No se puede contactar con el microservicio de especies: " + e.getMessage());
        }

    }

    public InventarioResponseDTO descontarStock(InventarioRequestDTO re){
        Ubicacion ubi = ubicacionRe
        .findByPasilloIdAndEstanteId(re.getIdPasillo(), re.getIdEstante())
        .orElseThrow(() -> new NoSuchElementException("No se encontro la ubicacion del Objeto con el id" + re.getIdPasillo() + re.getIdEstante() ));

        Inventario registro = inventarioRe
        .findByIdProductoAndUbicacionId(validarProd(re.getIdProd()), ubi.getIdPasEst())
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
