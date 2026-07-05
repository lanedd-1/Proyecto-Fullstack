package com.semestral.venta.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.venta.client.InventarioClient;
import com.semestral.venta.client.ProductoClient;
import com.semestral.venta.exception.ExternalServiceException;
import com.semestral.venta.exception.ResourceNotFoundException;
import com.semestral.venta.dto.DetalleRequestDTO;
import com.semestral.venta.dto.DetalleResponseDTO;
import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.DetalleRepository;
import com.semestral.venta.repository.VentaRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleService {

    private final DetalleRepository detalleRe;
    private final VentaRepository ventaRe;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;

    public List<DetalleResponseDTO> obtenerTodos() {
        return detalleRe.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public DetalleResponseDTO obtenerPorId(Long id) {
        Detalle d = detalleRe.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
        return convertToDTO(d);
    }

    public DetalleResponseDTO crearDetalle(DetalleRequestDTO dto) {
        if (dto.getVentaId() == null) {
            throw new IllegalArgumentException("ventaId es obligatorio para crear un detalle");
        }
        if (dto.getProductoId() == null) {
            throw new IllegalArgumentException("productoId es obligatorio para crear un detalle");
        }
        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser mayor a 0");
        }

        Venta venta = ventaRe.findById(dto.getVentaId())
            .orElseThrow(() -> new ResourceNotFoundException(dto.getVentaId()));

        Map<String, Object> producto;
        try {
            producto = productoClient.obtenerPorId(dto.getProductoId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(dto.getProductoId());
        } catch (FeignException e) {
            throw new ExternalServiceException("El microservicio de Productos no se encuentra disponible en este momento.");
        }

        if (producto == null || producto.get("idProd") == null) {
            throw new ResourceNotFoundException(dto.getProductoId());
        }

        Double precioProducto = extraerPrecioProducto(producto);
        descontarStockPorProducto(dto.getProductoId(), dto.getCantidad());
        Double subTotalCalculado = dto.getCantidad() * precioProducto;

    
        Optional<Detalle> detalleExistente = detalleRe.findByIdVenta_IdVentaAndProductoId(dto.getVentaId(), dto.getProductoId());
        
        Detalle detalle;
        if (detalleExistente.isPresent()) {
            detalle = detalleExistente.get();
            Double subTotalAnterior = detalle.getSubTotal();
            Integer nuevaCantidad = detalle.getCantidad() + dto.getCantidad();
            Double nuevoSubTotal = nuevaCantidad * precioProducto;
            
            detalle.setCantidad(nuevaCantidad);
            detalle.setSubTotal(nuevoSubTotal);
            detalle = detalleRe.save(detalle);
            

            Double totalActual = venta.getTotal() != null ? venta.getTotal() : 0.0;
            venta.setTotal(totalActual - subTotalAnterior + nuevoSubTotal);
            ventaRe.save(venta);
        } else {

            detalle = new Detalle();
            detalle.setCantidad(dto.getCantidad());
            detalle.setSubTotal(subTotalCalculado);
            detalle.setIdVenta(venta);
            detalle.setProductoId(dto.getProductoId());
            detalle = detalleRe.save(detalle);
            

            Double totalActual = venta.getTotal() != null ? venta.getTotal() : 0.0;
            venta.setTotal(totalActual + subTotalCalculado);
            ventaRe.save(venta);
        }
        
        return convertToDTO(detalle);
    }

    public DetalleResponseDTO actualizarDetalle(Long id, DetalleRequestDTO dto) {
        Detalle existente = detalleRe.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

        Double subTotalAnterior = existente.getSubTotal();
        Long ventaOriginalId = existente.getIdVenta() != null ? existente.getIdVenta().getIdVenta() : null;
        Venta ventaOriginal = existente.getIdVenta();

        existente.setCantidad(dto.getCantidad());

        Long productoId = dto.getProductoId() != null ? dto.getProductoId() : existente.getProductoId();
        if (productoId == null) {
            throw new IllegalArgumentException("El productoId es obligatorio");
        }

        Map<String, Object> producto;
        try {
            producto = productoClient.obtenerPorId(productoId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(productoId);
        } catch (FeignException e) {
            throw new ExternalServiceException("El microservicio de Productos no se encuentra disponible en este momento.");
        }

        if (producto == null || producto.get("idProd") == null) {
            throw new ResourceNotFoundException(productoId);
        }
        existente.setProductoId(productoId);

        Double precioProducto = extraerPrecioProducto(producto);
        Double subTotalNuevo = dto.getCantidad() * precioProducto;
        existente.setSubTotal(subTotalNuevo);

        Venta ventaDestino = ventaOriginal;
        if (dto.getVentaId() != null && !dto.getVentaId().equals(ventaOriginalId)) {
            ventaDestino = ventaRe.findById(dto.getVentaId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getVentaId()));
            existente.setIdVenta(ventaDestino);
        }

        Detalle actualizado = detalleRe.save(existente);

        if (ventaOriginal != null) {
            Double totalOriginal = ventaOriginal.getTotal() != null ? ventaOriginal.getTotal() : 0.0;
            ventaOriginal.setTotal(totalOriginal - subTotalAnterior);
            ventaRe.save(ventaOriginal);
        }

        if (ventaDestino != null) {
            Double totalDestino = ventaDestino.getTotal() != null ? ventaDestino.getTotal() : 0.0;
            ventaDestino.setTotal(totalDestino + subTotalNuevo);
            ventaRe.save(ventaDestino);
        }

        return convertToDTO(actualizado);
    }

    public void eliminarDetalle(Long id) {
        Detalle detalle = detalleRe.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
        
        if (detalle.getIdVenta() != null) {
            Venta venta = detalle.getIdVenta();
            Double totalActual = venta.getTotal() != null ? venta.getTotal() : 0.0;
            venta.setTotal(totalActual - detalle.getSubTotal());
            ventaRe.save(venta);
        }
        
        detalleRe.deleteById(id);
    }

    public DetalleResponseDTO convertToDTO(Detalle d) {
        DetalleResponseDTO dto = new DetalleResponseDTO(
            d.getIdDetalle(),
            d.getCantidad(),
            d.getSubTotal(),
            d.getIdVenta() != null ? d.getIdVenta().getIdVenta() : null,
            d.getProductoId(),
            null
        );
        if (d.getProductoId() != null) {
            try {
                Map<String, Object> producto = productoClient.obtenerPorId(d.getProductoId());
                dto.setProductoNombre(getNombreProducto(producto));
            } catch (Exception e) {
            }
        }
        return dto;
    }

    private String getNombreProducto(Map<String, Object> producto) {
        if (producto == null) {
            return null;
        }
        Object nombre = producto.get("nombreProd");
        return nombre != null ? nombre.toString() : null;
    }

    private Double extraerPrecioProducto(Map<String, Object> producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        
        Double precio = null;
        
        if (producto.containsKey("precio")) {
            precio = convertToDouble(producto.get("precio"));
        } else if (producto.containsKey("precioProd")) {
            precio = convertToDouble(producto.get("precioProd"));
        } else if (producto.containsKey("precioUnitario")) {
            precio = convertToDouble(producto.get("precioUnitario"));
        }
        
        if (precio == null || precio <= 0) {
            throw new IllegalArgumentException("El producto no tiene un precio válido");
        }
        
        return precio;
    }

    private Double convertToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void descontarStockPorProducto(Long productoId, Integer cantidad) {
        if (productoId == null || cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("Para descontar stock se requieren idProd y cantidad mayor a 0");
        }

        Map<String, Object> request = new java.util.HashMap<>();
        request.put("idProd", productoId);
        request.put("cantidad", cantidad);

        try {
            inventarioClient.descontarStockPorProducto(request);
        } catch (FeignException.BadRequest e) {
            throw new IllegalArgumentException("No hay suficientes existencias en inventario para el producto con ID: " + productoId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(productoId);
        } catch (FeignException e) {
            throw new ExternalServiceException("El microservicio de Inventario no se encuentra disponible en este momento.");
        }
    }
}