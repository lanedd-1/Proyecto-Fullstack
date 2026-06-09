package com.semestral.productos.service;

import java.math.BigDecimal;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.dto.ProductoResponseDTO;
import com.semestral.productos.exception.ResourceNotFoundException;
import com.semestral.productos.model.Categoria;
import com.semestral.productos.model.Productos;
import com.semestral.productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    
    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;


    public List<ProductoResponseDTO> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> encontrarPorId(Long id){
        return productoRepository.findById(id).map(this::convertToDTO);
    }
    
    public ProductoResponseDTO saveProducto(ProductoRequestDTO productos) {
        if (productos.getIdCat() == null) {
            throw new IllegalArgumentException("El id de categoría es obligatorio");
        }
        Categoria categoria = categoriaService.findById(productos.getIdCat())
                .orElseThrow(() -> new ResourceNotFoundException(productos.getIdCat()));

        Productos prod = new Productos(
            null,
            productos.getSku(),
            productos.getNombreProd(),
            productos.getDescProd(),
            productos.getPrecioUnitario(),
            productos.getFoto(),
            productos.getStock(),
            categoria
        );

        Productos guardado = productoRepository.save(prod);
        return convertToDTO(guardado);
    }


    private ProductoResponseDTO convertToDTO(Productos p) {
        return new ProductoResponseDTO(
            p.getIdProd(),
            p.getSku(),
            p.getNombreProd(),
            p.getDescProd(),
            p.getPrecioUnitario(),
            p.getFoto(),
            p.getStock(),
            p.getIdCat()
        );
    }

    public List<ProductoResponseDTO> findBySku(String sku) {
        return productoRepository.encontrarProductosPorSku(sku).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void eliminarProd (Long id){
          productoRepository.deleteById(id);
    }

    public ProductoResponseDTO updatePrecio(Long id, BigDecimal precio) {
        Productos prod = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        prod.setPrecioUnitario(precio);
        Productos saved = productoRepository.save(prod);
        return convertToDTO(saved);
    }

    public ProductoResponseDTO updateDescripcion(Long id, String descripcion) {
        Productos prod = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        if (descripcion != null && !descripcion.isBlank()) {
            prod.setDescProd(descripcion);
        }
        Productos saved = productoRepository.save(prod);
        return convertToDTO(saved);
    }

    public ProductoResponseDTO updateStock(Long id, Long stock) {
        Productos prod = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        if (stock != null) {
            prod.setStock(stock);
        }
        Productos saved = productoRepository.save(prod);
        return convertToDTO(saved);
    }
}