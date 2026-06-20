package com.joyeria.gestion_configuracion.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.joyeria.gestion_configuracion.client.UsuarioClient;
import com.joyeria.gestion_configuracion.dto.ConfiguracionRequestDTO;
import com.joyeria.gestion_configuracion.dto.ConfiguracionResponseDTO;
import com.joyeria.gestion_configuracion.dto.UsuarioResponseDTO;
import com.joyeria.gestion_configuracion.exception.ConfiguracionLongitudInvalidaException;
import com.joyeria.gestion_configuracion.exception.ConfiguracionNotFoundException;
import com.joyeria.gestion_configuracion.model.Configuracion;
import com.joyeria.gestion_configuracion.repository.ConfiguracionRepository;
import com.joyeria.gestion_configuracion.service.ConfiguracionService;

import feign.FeignException;
import feign.Request;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unit de ConfiguracionService")
public class ConfiguracionServiceTest {


    @Mock
    private ConfiguracionRepository configuracionRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ConfiguracionService configuracionService;

    private Configuracion configEjemplo;
    private ConfiguracionRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        configEjemplo = new Configuracion(1L, 8, 20, true, true, true, true, "!@#$%&*");
        dtoValido = new ConfiguracionRequestDTO(8, 20, true, true, true, true, "!@#$%&*");
    }



    @Test
    @DisplayName("getConfiguracion() debe retornar la configuracion junto a los usuarios de ms-usuario")
    void getConfiguracion_debeRetornarConfig_conUsuarios() {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(1L, "Ana", "11.111.111-1", "ana@mail.com", 1L, "Admin");

        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(configEjemplo));
        when(usuarioClient.getAllUsuarios()).thenReturn(List.of(usuario));

        ConfiguracionResponseDTO resultado = configuracionService.getConfiguracion();

        assertNotNull(resultado);
        assertEquals(8, resultado.getLongitudMinima());
        assertEquals(1, resultado.getUsuarios().size());
        assertEquals("Ana", resultado.getUsuarios().get(0).getNombreUser());
    }

    @Test
    @DisplayName("getConfiguracion() debe lanzar ConfiguracionNotFoundException cuando no existe en la BD")
    void getConfiguracion_debeLanzarExcepcion_cuandoNoExiste() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ConfiguracionNotFoundException.class, () -> configuracionService.getConfiguracion());
    }

    @Test
    @DisplayName("getConfiguracion() debe retornar lista de usuarios vacia cuando ms-usuario no esta disponible")
    void getConfiguracion_debeRetornarListaVacia_siMsUsuarioNoDisponible() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(configEjemplo));
        when(usuarioClient.getAllUsuarios()).thenThrow(
                new FeignException.ServiceUnavailable("ms-usuario no disponible", buildDummyRequest(), null, null));

        ConfiguracionResponseDTO resultado = configuracionService.getConfiguracion();

        assertNotNull(resultado);
        assertTrue(resultado.getUsuarios().isEmpty());
    }

    // TEST UNIT - update()

    @Test
    @DisplayName("update() debe actualizar la configuracion cuando los datos son validos")
    void update_debeActualizarConfiguracion_cuandoDatosValidos() {
        Configuracion actualizada = new Configuracion(1L, 10, 25, true, true, true, true, "!@#$");

        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(configEjemplo));
        when(configuracionRepository.save(org.mockito.ArgumentMatchers.any(Configuracion.class)))
                .thenReturn(actualizada);

        ConfiguracionRequestDTO dtoActualizado = new ConfiguracionRequestDTO(10, 25, true, true, true, true, "!@#$");
        ConfiguracionResponseDTO resultado = configuracionService.update(dtoActualizado);

        assertNotNull(resultado);
        assertEquals(10, resultado.getLongitudMinima());
        assertEquals(25, resultado.getLongitudMaxima());
        verify(configuracionRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Configuracion.class));
    }

    @Test
    @DisplayName("update() debe lanzar ConfiguracionLongitudInvalidaException cuando minima >= maxima")
    void update_debeLanzarExcepcion_cuandoLongitudMinimaMayorOIgualQueMaxima() {
        ConfiguracionRequestDTO dtoInvalido = new ConfiguracionRequestDTO(20, 20, true, true, true, true, "!@#$");

        assertThrows(ConfiguracionLongitudInvalidaException.class,
                () -> configuracionService.update(dtoInvalido));

        verify(configuracionRepository, times(0)).save(org.mockito.ArgumentMatchers.any(Configuracion.class));
    }

    @Test
    @DisplayName("update() debe lanzar ConfiguracionNotFoundException cuando no existe la configuracion en la BD")
    void update_debeLanzarExcepcion_cuandoNoExisteConfiguracion() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ConfiguracionNotFoundException.class, () -> configuracionService.update(dtoValido));
    }

    // --- AUXILIAR para simular fallos de Feign ---
    private Request buildDummyRequest() {
        return Request.create(Request.HttpMethod.GET, "/api/usuarios",
                java.util.Collections.emptyMap(), Request.Body.empty(), null);
    }
}
