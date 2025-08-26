package com.example.pharmacy.controllers;

import com.example.pharmacy.model.Medicamento;
import com.example.pharmacy.service.MedicamentoService;
import com.example.pharmacy.dto.MedicamentoListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*; 

//prueba
@ExtendWith(MockitoExtension.class)
class MedicamentoControllerTest {

    @Mock
    private MedicamentoService medicamentoService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MedicamentoController medicamentoController;

    private Medicamento testMedicamento;
    private List<Medicamento> testMedicamentos;

    @BeforeEach
    void setUp() {
        testMedicamento = new Medicamento();
        testMedicamento.setIdMedicamento(1L);
        testMedicamento.setNombre("Paracetamol");
        testMedicamento.setDescripcion("Analgésico");
        testMedicamento.setPrecio(10.0);
        testMedicamento.setStock(100);
        testMedicamento.setRequiereReceta("N");

        testMedicamentos = Arrays.asList(testMedicamento);
    }

    @Test
    void testFindAll() {
        // Arrange
        when(medicamentoService.findAll()).thenReturn(testMedicamentos);

        // Act
        ResponseEntity<List<Medicamento>> response = medicamentoController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMedicamentos, response.getBody());
        verify(medicamentoService).findAll();
    }

    @Test
    void testGetById() {
        // Arrange
        Long id = 1L;
        when(medicamentoService.findById(id)).thenReturn(testMedicamento);

        // Act
        ResponseEntity<Medicamento> response = medicamentoController.getById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMedicamento, response.getBody());
        verify(medicamentoService).findById(id);
    }

    @Test
    void testCreate_Success() throws Exception {
        // Arrange
        when(objectMapper.writeValueAsString(any(Medicamento.class))).thenReturn("{}");
        when(medicamentoService.create(any(Medicamento.class))).thenReturn(testMedicamento);

        // Act
        ResponseEntity<Medicamento> response = medicamentoController.create(testMedicamento);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testMedicamento, response.getBody());
        verify(medicamentoService).create(testMedicamento);
        verify(objectMapper).writeValueAsString(testMedicamento);
    }

    @Test
    void testCreate_Exception() throws Exception {
        // Arrange
        when(objectMapper.writeValueAsString(any(Medicamento.class))).thenReturn("{}");
        when(medicamentoService.create(any(Medicamento.class))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> medicamentoController.create(testMedicamento));
        verify(medicamentoService).create(testMedicamento);
    }

    @Test
    void testUpdate_Success() throws Exception {
        // Arrange
        Long id = 1L;
        when(objectMapper.writeValueAsString(any(Medicamento.class))).thenReturn("{}");
        when(medicamentoService.update(eq(id), any(Medicamento.class))).thenReturn(testMedicamento);

        // Act
        ResponseEntity<Medicamento> response = medicamentoController.update(id, testMedicamento);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMedicamento, response.getBody());
        verify(medicamentoService).update(id, testMedicamento);
        verify(objectMapper).writeValueAsString(testMedicamento);
    }

    @Test
    void testUpdate_Exception() throws Exception {
        // Arrange
        Long id = 1L;
        when(objectMapper.writeValueAsString(any(Medicamento.class))).thenReturn("{}");
        when(medicamentoService.update(eq(id), any(Medicamento.class))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> medicamentoController.update(id, testMedicamento));
        verify(medicamentoService).update(id, testMedicamento);
    }

    @Test
    void testDelete() {
        // Arrange
        Long id = 1L;
        doNothing().when(medicamentoService).delete(id);

        // Act
        ResponseEntity<Void> response = medicamentoController.delete(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(medicamentoService).delete(id);
    }

    @Test
    void testSearchMedicamento() {
        // Arrange
        String searchTerm = "para";
        when(medicamentoService.search(searchTerm)).thenReturn(testMedicamentos);

        // Act
        List<Medicamento> result = medicamentoController.searchMedicamento(searchTerm);

        // Assert
        assertEquals(testMedicamentos, result);
        verify(medicamentoService).search(searchTerm);
    }

    @Test
    void testGetLastMedicamentos() {
        // Arrange
        when(medicamentoService.findLastTen()).thenReturn(testMedicamentos);

        // Act
        List<Medicamento> result = medicamentoController.getLastMedicamentos();

        // Assert
        assertEquals(testMedicamentos, result);
        verify(medicamentoService).findLastTen();
    }

    @Test
    void testExportMedicamentosXml() throws IOException {
        // Arrange
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(medicamentoService.findAll()).thenReturn(testMedicamentos);

        // Act
        medicamentoController.exportMedicamentosXml(response);

        // Assert
        assertEquals("application/xml", response.getContentType());
        assertEquals("attachment; filename=medicamentos.xml", response.getHeader("Content-Disposition"));
        verify(medicamentoService).findAll();
        
        // Verify response is not empty
        assertTrue(response.getContentAsString().length() > 0);
    }





    @Test
    void testImportMedicamentosXml_IOException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "invalid.xml", 
            "application/xml", 
            "invalid xml content".getBytes()
        );

        // Act
        ResponseEntity<String> response = medicamentoController.importMedicamentosXml(file);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().startsWith("Error en la importación:"));
    }



    @Test
    void testCreate_WithRequiereRecetaNull() throws Exception {
        // Arrange
        testMedicamento.setRequiereReceta(null);
        when(objectMapper.writeValueAsString(any(Medicamento.class))).thenReturn("{}");
        when(medicamentoService.create(any(Medicamento.class))).thenReturn(testMedicamento);

        // Act
        ResponseEntity<Medicamento> response = medicamentoController.create(testMedicamento);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testMedicamento, response.getBody());
        verify(medicamentoService).create(testMedicamento);
        verify(objectMapper).writeValueAsString(testMedicamento);
    }

    @Test
    void testCreate_WithRequiereRecetaString() throws Exception {
        // Arrange
        testMedicamento.setRequiereReceta("Y");
        when(objectMapper.writeValueAsString(any(Medicamento.class))).thenReturn("{}");
        when(medicamentoService.create(any(Medicamento.class))).thenReturn(testMedicamento);

        // Act
        ResponseEntity<Medicamento> response = medicamentoController.create(testMedicamento);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testMedicamento, response.getBody());
        verify(medicamentoService).create(testMedicamento);
        verify(objectMapper).writeValueAsString(testMedicamento);
    }
}
