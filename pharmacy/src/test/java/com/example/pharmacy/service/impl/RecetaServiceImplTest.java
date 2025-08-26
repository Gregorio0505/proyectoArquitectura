package com.example.pharmacy.service.impl;

import com.example.pharmacy.model.Receta;
import com.example.pharmacy.model.RecetaDetalle;
import com.example.pharmacy.dto.RecetaDTO;
import com.example.pharmacy.dto.RecetaDetalleDTO;
import com.example.pharmacy.repository.RecetaRepository;
import com.example.pharmacy.repository.RecetaDetalleRepository;
import com.example.pharmacy.service.AuditoriaService;
import com.example.pharmacy.util.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private RecetaDetalleRepository detalleRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private RecetaServiceImpl service;

    private Receta testReceta;
    private RecetaDTO testRecetaDTO;
    private RecetaDetalle testDetalle;
    private RecetaDetalleDTO testDetalleDTO;

    @BeforeEach
    void setUp() {
        testReceta = new Receta();
        testReceta.setIdReceta(1L);
        testReceta.setCodigoReceta("REC001");
        testReceta.setFecha(LocalDateTime.now());
        testReceta.setIdUsuario(1L);
        testReceta.setAprobadoSeguro("N");
        testReceta.setPdfUrl("test.pdf");

        testDetalle = new RecetaDetalle();
        testDetalle.setIdDetalle(1L);
        testDetalle.setIdReceta(1L);
        testDetalle.setIdMedicamento(1L);
        testDetalle.setDosis("1 comprimido");
        testDetalle.setFrecuencia("cada 8 horas");
        testDetalle.setDuracion("7 días");
        testDetalle.setCantidadRequerida(21);
        testDetalle.setObservaciones("Tomar con comida");

        testDetalleDTO = new RecetaDetalleDTO();
        testDetalleDTO.setIdDetalle(1L);
        testDetalleDTO.setIdMedicamento(1L);
        testDetalleDTO.setDosis("1 comprimido");
        testDetalleDTO.setFrecuencia("cada 8 horas");
        testDetalleDTO.setDuracion("7 días");
        testDetalleDTO.setCantidadRequerida(21);
        testDetalleDTO.setObservaciones("Tomar con comida");

        testRecetaDTO = new RecetaDTO();
        testRecetaDTO.setIdReceta(1L);
        testRecetaDTO.setCodigoReceta("REC001");
        testRecetaDTO.setIdUsuario(1L);
        testRecetaDTO.setPdfUrl("test.pdf");
        testRecetaDTO.setDetalles(Arrays.asList(testDetalleDTO));
    }

    @Test
    void testCreateReceta_Success() {
        // Arrange
        when(recetaRepository.save(any(Receta.class))).thenReturn(testReceta);
        when(detalleRepository.save(any(RecetaDetalle.class))).thenReturn(testDetalle);
        when(userDetails.getUsuarioActual()).thenReturn("testuser");
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(testReceta));
        when(detalleRepository.findByIdReceta(1L)).thenReturn(Arrays.asList(testDetalle));

        // Act
        RecetaDTO result = service.createReceta(testRecetaDTO);

        // Assert
        assertNotNull(result);
        assertEquals("REC001", result.getCodigoReceta());
        assertEquals(1L, result.getIdUsuario());
        verify(recetaRepository).save(any(Receta.class));
        verify(detalleRepository, times(1)).save(any(RecetaDetalle.class));
    }

    @Test
    void testGetRecetaWithDetails_Success() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(testReceta));
        when(detalleRepository.findByIdReceta(1L)).thenReturn(Arrays.asList(testDetalle));

        // Act
        RecetaDTO result = service.getRecetaWithDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdReceta());
        assertEquals("REC001", result.getCodigoReceta());
        assertEquals(1, result.getDetalles().size());
        verify(recetaRepository).findById(1L);
        verify(detalleRepository).findByIdReceta(1L);
    }

    @Test
    void testGetRecetaWithDetails_NotFound() {
        // Arrange
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            service.getRecetaWithDetails(1L);
        });

        assertEquals("Receta no encontrada: 1", exception.getMessage());
    }


}
