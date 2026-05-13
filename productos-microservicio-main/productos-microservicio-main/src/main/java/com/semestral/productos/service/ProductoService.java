package com.semestral.productos.service;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.dto.ProductoResponseDTO;
import com.semestral.productos.model.Categoria;
import com.semestral.productos.model.Productos;
import com.semestral.productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    
    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    // Convertimos la lista de entidades a lista de DTOs
    public List<ProductoResponseDTO> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> encontrarPorId(Long id){
        return productoRepository.findById(id).map(this::convertToDTO);
    }
    
    // Recibe entidad (validada en Controller) y devuelve DTO
    public ProductoResponseDTO saveProducto(ProductoRequestDTO productos) {
        if (productos.getIdCat() == null) {
            throw new RuntimeException("El id es obligatorio");
        }
        Categoria categoria = categoriaService.findById(productos.getIdCat())
                .orElseThrow(() -> new RuntimeException("La categoría ID: " + productos.getIdCat() + " no existe"));

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


    // Método Helper para mapear
    private ProductoResponseDTO convertToDTO(Productos p) {
        return new ProductoResponseDTO(
            p.getId(),
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
}