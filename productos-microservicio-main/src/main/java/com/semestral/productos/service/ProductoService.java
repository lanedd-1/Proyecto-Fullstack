package com.semestral.productos.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.semestral.productos.model.Productos;
import com.semestral.productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Productos> getAllProductos() {
        return productoRepository.findAll();
    }

    public Productos getProductoById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con id: " + id));
    }

    public Productos createProducto(Productos producto) {
        return productoRepository.save(producto);
    }

    public Productos updateProducto(Long id, Productos producto) {
        Productos existente = getProductoById(id);
        producto.setId_producto(existente.getId_producto());
        return productoRepository.save(producto);
    }

    public void deleteProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new NoSuchElementException("Producto no existe con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<Productos> findByNombreIgnoreCase(String nombre) {
        return productoRepository.findByNombre_prodIgnoreCase(nombre);
    }

    public List<Productos> findByPrecioMax(BigDecimal precio) {
        return productoRepository.findByPrecio_unitarioLessThanEqual(precio);
    }

    public List<Productos> findBySku(String sku) {
        return productoRepository.encontrarProductosPorSku(sku);
    }

    public List<Productos> findByNombreContains(String nombre) {
        return productoRepository.encontrarProductosPorNombre(nombre);
    }
}
