package com.semestral.productos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.semestral.productos.model.Categoria;
import com.semestral.productos.model.Productos;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test del repositorio de productos en memoria")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Productos collarOro;
    private Productos anilloPlata;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = entityManager.persistAndFlush(new Categoria(null, "Collares"));

        collarOro = entityManager.persistAndFlush(
            new Productos(
                null,
                "80818902",
                "Collar de oro 9 kilates",
                "Collar de oro 9 kilates con detalles bordados en plata",
                BigDecimal.valueOf(820.00),
                "https://images.pexels.com/photos/16304608/pexels-photo-16304608.jpeg",
                categoria
            )
        );

        anilloPlata = entityManager.persistAndFlush(
            new Productos(
                null,
                "90012345",
                "Anillo de plata",
                "Anillo de plata 925 para mujer",
                BigDecimal.valueOf(320.00),
                "https://images.pexels.com/photos/12345678/pexels-photo-12345678.jpeg",
                categoria
            )
        );
    }

    @Test
    @DisplayName("Buscar producto por nombre ignorando mayúsculas/minúsculas")
    void whenFindByNombreProdIgnoreCase_thenReturnMatchingProducts() {
        List<Productos> productos = productoRepository.findByNombreProdIgnoreCase("COLLAR DE ORO 9 KILATES");

        assertNotNull(productos);
        assertEquals(1, productos.size());
        assertEquals(collarOro.getSku(), productos.get(0).getSku());
    }

    @Test
    @DisplayName("Buscar productos por precio menor o igual al umbral")
    void whenFindByPrecioUnitarioLessThanEqual_thenReturnAffordableProducts() {
        List<Productos> productos = productoRepository.findByPrecioUnitarioLessThanEqual(BigDecimal.valueOf(500.00));

        assertNotNull(productos);
        assertTrue(productos.size() == 1, "Se debe encontrar exactamente un producto affordable");
        assertEquals(anilloPlata.getSku(), productos.get(0).getSku());
    }

    @Test
    @DisplayName("Buscar producto por SKU usando consulta personalizada")
    void whenFindBySku_thenReturnProduct() {
        List<Productos> productos = productoRepository.encontrarProductosPorSku("80818902");

        assertNotNull(productos);
        assertTrue(productos.size() > 0, "Debe devolver al menos un producto con el SKU solicitado");
        assertEquals(collarOro.getNombreProd(), productos.get(0).getNombreProd());
    }
}
