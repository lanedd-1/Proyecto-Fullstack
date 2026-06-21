package com.semestral.inventario.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.inventario.client.ProductosClient;
import com.semestral.inventario.dto.EstanteRequestDTO;
import com.semestral.inventario.dto.DescontarProductoRequestDTO;
import com.semestral.inventario.dto.InventarioRequestDTO;
import com.semestral.inventario.dto.InventarioResponseDTO;
import com.semestral.inventario.dto.PasilloRequestDTO;
import com.semestral.inventario.dto.ProductoDTO;
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
import com.semestral.inventario.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioStockRepository inventarioRe;
    private final UbicacionRepository ubicacionRe;
    private final PasilloRepository pasillRe;
    private final EstanteRepository estanteRe;
    private final ProductosClient prodCli;
    
    private ProductoDTO productoActual;

    public InventarioResponseDTO agregarStock(InventarioRequestDTO re){

        Ubicacion ubicacion = findOrCreateUbicacion(re.getIdPasillo(), re.getIdEstante());

        Optional<Inventario> stockExistente = inventarioRe.findByProductoYUbicacion(validarProd(re.getIdProd()), ubicacion.getIdPasEst());

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

        return ubicacionRe.findByPasilloAndEstante(pasillo.getIdPasillo(), estante.getIdEstante())
            .orElseGet(() -> ubicacionRe.save(new Ubicacion(null, pasillo, estante)));
    }

    private Ubicacion findOrCreateUbicacion(Long idPasillo, Long idEstante) {
        Pasillo pasillo = pasillRe.findById(idPasillo)
            .orElseThrow(() -> new NoSuchElementException("Pasillo no encontrado"));
        Estante estante = estanteRe.findById(idEstante)
            .orElseThrow(() -> new NoSuchElementException("Estante no encontrado"));

        return ubicacionRe.findByPasilloAndEstante(idPasillo, idEstante)
            .orElseGet(() -> ubicacionRe.save(new Ubicacion(null, pasillo, estante)));
    }

    public Long validarProd(Long idProd){
        try {
            this.productoActual = prodCli.obtenerProducto(idProd);
            return idProd;
            
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(idProd);
        } catch (FeignException e){
            throw new RuntimeException("No se puede contactar con el microservicio de productos: " + e.getMessage());
        }

    }

    public InventarioResponseDTO descontarStock(InventarioRequestDTO re){
        Ubicacion ubi = ubicacionRe
        .findByPasilloAndEstante(re.getIdPasillo(), re.getIdEstante())
        .orElseThrow(() -> new NoSuchElementException("No se encontro la ubicacion del Objeto con el id" + re.getIdPasillo() + re.getIdEstante() ));

        Inventario registro = inventarioRe
        .findByProductoYUbicacion(validarProd(re.getIdProd()), ubi.getIdPasEst())
        .orElseThrow(() -> new NoSuchElementException("El producto no registra stock en esta ubicacion"));


        if (registro.getStock() < re.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente en esta ubicación. Disponible: " 
                    + registro.getStock() + ", Solicitado: " + re.getCantidad());
    }

    registro.setStock(registro.getStock() - re.getCantidad());
        Inventario actualizado = inventarioRe.save(registro);
        return convertToDTO(actualizado);
}

    public InventarioResponseDTO descontarStockPorProducto(DescontarProductoRequestDTO request) {
        validarProd(request.getIdProd());
        Inventario registro = inventarioRe.findByIdProd(request.getIdProd()).stream()
            .filter(stock -> stock.getStock() != null && stock.getStock() >= request.getCantidad())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No hay suficientes existencias en inventario para el producto ID: " + request.getIdProd()));

        registro.setStock(registro.getStock() - request.getCantidad());
        Inventario actualizado = inventarioRe.save(registro);
        return convertToDTO(actualizado);
}

public List<InventarioResponseDTO> getStockPorProducto (Long idPps){

    validarProd(idPps);
    return inventarioRe.findByIdProd(idPps).stream()
    .map(this::convertToDTOWithProduct).collect(Collectors.toList());
}

public List<InventarioResponseDTO> getTodoStock(){
return inventarioRe.findAll().stream().map(this::convertToDTOWithProduct).collect(Collectors.toList());

}


    private InventarioResponseDTO convertToDTO(Inventario s) {
        return new InventarioResponseDTO(
            s.getIdInv(),
            s.getIdProd(),
            productoActual != null ? productoActual.getSku() : "N/A",
            productoActual != null ? productoActual.getNombreProd() : "N/A",
            productoActual != null ? productoActual.getPrecioUnitario() : 0.0,
            s.getIdPasEstante().getIdPasillo().getNombrePasillo(),
            s.getIdPasEstante().getIdEstante().getNombreEstante(),
            s.getStock()
        );
    }

    private InventarioResponseDTO convertToDTOWithProduct(Inventario s) {
        try {
            ProductoDTO producto = prodCli.obtenerProducto(s.getIdProd());
            log.info("Producto obtenido: {}", producto);
            return new InventarioResponseDTO(
                s.getIdInv(),
                s.getIdProd(),
                producto.getSku(),
                producto.getNombreProd(),
                producto.getPrecioUnitario(),
                s.getIdPasEstante().getIdPasillo().getNombrePasillo(),
                s.getIdPasEstante().getIdEstante().getNombreEstante(),
                s.getStock()
            );
        } catch (Exception e) {
            log.error("Error al obtener producto con ID: {}, Error: {}", s.getIdProd(), e.getMessage(), e);
            return new InventarioResponseDTO(
                s.getIdInv(),
                s.getIdProd(),
                "N/A",
                "Producto no disponible",
                0.0,
                s.getIdPasEstante().getIdPasillo().getNombrePasillo(),
                s.getIdPasEstante().getIdEstante().getNombreEstante(),
                s.getStock()
            );
        }
    }
}
