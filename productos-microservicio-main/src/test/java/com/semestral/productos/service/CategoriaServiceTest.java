package com.semestral.productos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.semestral.productos.dto.CategoriaRequestDTO;
import com.semestral.productos.dto.CategoriaResponseDTO;
import com.semestral.productos.model.Categoria;
import com.semestral.productos.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unit de CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaEjemplo;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria(1L, "Collares");
    }

    @Test
    @DisplayName("findAllCat() retorna todas las categorías como DTO")
    void findAllCat_debeRetornarListaDeCategoriaResponseDTO() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaEjemplo));

        List<CategoriaResponseDTO> resultado = categoriaService.findAllCat();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdCat());
        assertEquals("Collares", resultado.get(0).getNombreCat());

        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("saveCat() guarda una nueva categoría y devuelve DTO")
    void saveCat_debeGuardarCategoriaYRetornarDTO() {
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria cat = invocation.getArgument(0);
            cat.setIdCat(2L);
            return cat;
        });

        CategoriaRequestDTO request = new CategoriaRequestDTO("Anillos");
        CategoriaResponseDTO resultado = categoriaService.saveCat(request);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getIdCat());
        assertEquals("Anillos", resultado.getNombreCat());

        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("buscarPorId() devuelve DTO cuando la categoría existe")
    void buscarPorId_debeRetornarOptionalResponseDTOCuandoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));

        Optional<CategoriaResponseDTO> resultado = categoriaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCat());
        assertEquals("Collares", resultado.get().getNombreCat());

        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() delega en el repositorio")
    void findById_debeDelegarAlRepositorio() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));

        Optional<Categoria> resultado = categoriaService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCat());
        assertEquals("Collares", resultado.get().getNombreCat());

        verify(categoriaRepository, times(1)).findById(1L);
    }
}
