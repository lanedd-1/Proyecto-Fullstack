package com.semestral.productos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.semestral.productos.dto.ProductoRequestDTO;
import com.semestral.productos.dto.ProductoResponseDTO;
import com.semestral.productos.model.Categoria;
import com.semestral.productos.model.Productos;
import com.semestral.productos.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unit de ProductoService")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProductoService productoService;

    private Productos productoEjemplo;
    private Categoria categoriaEjemplo;
    private ProductoRequestDTO productoRequest;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria(1L, "Collares");

        productoEjemplo = new Productos(
            1L,
            "80818902",
            "Collar de oro 9 kilates",
            "Collar de oro 9 kilates con detalles bordados en plata",
            BigDecimal.valueOf(820.00),
            "https://images.pexels.com/photos/16304608/pexels-photo-16304608.jpeg",
            categoriaEjemplo
        );

        productoRequest = new ProductoRequestDTO(
            "80818902",
            "Collar de oro 9 kilates",
            "Collar de oro 9 kilates con detalles bordados en plata",
            BigDecimal.valueOf(820.00),
            "https://images.pexels.com/photos/16304608/pexels-photo-16304608.jpeg",
            1L
        );
    }

    @Test
    @DisplayName("getAllProductos() retorna todos los productos como DTO")
    void getAllProductos_debeRetornarListaDeProductoResponseDTO() {
        when(productoRepository.findAll()).thenReturn(List.of(productoEjemplo));

        List<ProductoResponseDTO> resultado = productoService.getAllProductos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("80818902", resultado.get(0).getSku());
        assertEquals("Collar de oro 9 kilates", resultado.get(0).getNombreProd());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findBySku() retorna los productos con el SKU solicitado")
    void findBySku_debeRetornarProductosPorSku() {
        when(productoRepository.encontrarProductosPorSku("80818902")).thenReturn(List.of(productoEjemplo));

        List<ProductoResponseDTO> resultado = productoService.findBySku("80818902");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("80818902", resultado.get(0).getSku());
        assertEquals("Collar de oro 9 kilates", resultado.get(0).getNombreProd());

        verify(productoRepository, times(1)).encontrarProductosPorSku("80818902");
    }

    @Test
    @DisplayName("saveProducto() guarda un producto nuevo cuando la categoría existe")
    void saveProducto_debeGuardarProductoCuandoCategoriaExiste() {
        when(categoriaService.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(productoRepository.save(any(Productos.class))).thenReturn(productoEjemplo);

        ProductoResponseDTO resultado = productoService.saveProducto(productoRequest);

        assertNotNull(resultado);
        assertEquals("80818902", resultado.getSku());
        assertEquals("Collar de oro 9 kilates", resultado.getNombreProd());
        assertNotNull(resultado.getIdCat());
        assertEquals("Collares", resultado.getIdCat().getNombreCat());

        verify(categoriaService, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Productos.class));
    }
}
