package com.example.pharmacy.controllers;

import com.example.pharmacy.dto.CheckoutDTO;
import com.example.pharmacy.dto.FacturaDTO;
import com.example.pharmacy.model.Usuario;
import com.example.pharmacy.service.CarritoService;
import com.example.pharmacy.service.FacturaService;
import com.example.pharmacy.service.UsuarioService;
import com.example.pharmacy.service.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @Mock
    private CarritoService carritoService;

    @Mock
    private FacturaService facturaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CheckoutController checkoutController;

    private Usuario testUsuario;
    private CheckoutDTO testCheckoutDTO;
    private FacturaDTO testFacturaDTO;

    @BeforeEach
    void setUp() {
        testUsuario = new Usuario();
        testUsuario.setIdUsuario(1L);
        testUsuario.setCorreo("test@example.com");

        testCheckoutDTO = new CheckoutDTO();
        testCheckoutDTO.setDescuento(10.0);
        testCheckoutDTO.setEmail("test@example.com");

        testFacturaDTO = new FacturaDTO();
        testFacturaDTO.setIdFactura(1L);
        testFacturaDTO.setIdVenta(1L);

        // Mock SecurityContext
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn("test@example.com");
        lenient().when(usuarioService.findByCorreo("test@example.com")).thenReturn(testUsuario);
    }

    @Test
    void testCheckout_Success() {
        // Arrange
        when(carritoService.checkoutWithDiscount(1L, 10.0)).thenReturn(testFacturaDTO);
        doNothing().when(facturaService).sendFacturaEmail(1L, "test@example.com");

        // Act
        ResponseEntity<?> response = checkoutController.checkout(testCheckoutDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testFacturaDTO, response.getBody());
        verify(carritoService).checkoutWithDiscount(1L, 10.0);
        verify(facturaService).sendFacturaEmail(1L, "test@example.com");
    }

    @Test
    void testCheckout_SuccessWithoutEmail() {
        // Arrange
        testCheckoutDTO.setEmail(null);
        when(carritoService.checkoutWithDiscount(1L, 10.0)).thenReturn(testFacturaDTO);

        // Act
        ResponseEntity<?> response = checkoutController.checkout(testCheckoutDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testFacturaDTO, response.getBody());
        verify(carritoService).checkoutWithDiscount(1L, 10.0);
        verify(facturaService, never()).sendFacturaEmail(anyLong(), anyString());
    }

    @Test
    void testCheckout_EmailError() {
        // Arrange
        when(carritoService.checkoutWithDiscount(1L, 10.0)).thenReturn(testFacturaDTO);
        doThrow(new RuntimeException("Email service error")).when(facturaService).sendFacturaEmail(1L, "test@example.com");

        // Act
        ResponseEntity<?> response = checkoutController.checkout(testCheckoutDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testFacturaDTO, response.getBody());
        verify(carritoService).checkoutWithDiscount(1L, 10.0);
        verify(facturaService).sendFacturaEmail(1L, "test@example.com");
        verify(auditoriaService).registrar("Checkout", "EMAIL_ERROR", 
            "Failed to send invoice email: Email service error", "usuario=1");
    }

    @Test
    void testCheckout_DatabaseError() {
        // Arrange
        when(carritoService.checkoutWithDiscount(1L, 10.0))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = checkoutController.checkout(testCheckoutDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Checkout failed", responseBody.get("error"));
        assertEquals("An unexpected error occurred during checkout: Database error", 
            responseBody.get("message"));
        
        verify(carritoService).checkoutWithDiscount(1L, 10.0);
    }

    @Test
    void testCheckout_IllegalStateError() {
        // Arrange
        when(carritoService.checkoutWithDiscount(1L, 10.0))
            .thenThrow(new IllegalStateException("Cart is empty"));

        // Act
        ResponseEntity<?> response = checkoutController.checkout(testCheckoutDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Invalid operation", responseBody.get("error"));
        assertEquals("Cart is empty", responseBody.get("message"));
        
        verify(carritoService).checkoutWithDiscount(1L, 10.0);
    }

    @Test
    void testCheckout_UnexpectedError() {
        // Arrange
        when(carritoService.checkoutWithDiscount(1L, 10.0))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = checkoutController.checkout(testCheckoutDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Checkout failed", responseBody.get("error"));
        assertEquals("An unexpected error occurred during checkout: Unexpected error", 
            responseBody.get("message"));
        assertEquals("java.lang.RuntimeException", responseBody.get("type"));
        
        verify(carritoService).checkoutWithDiscount(1L, 10.0);
    }

    @Test
    void testDownloadInvoicePdf_Success() throws Exception {
        // Arrange
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        pdfStream.write("PDF content".getBytes());
        when(facturaService.generatePdfFactura(1L)).thenReturn(pdfStream);

        // Act
        ResponseEntity<?> response = checkoutController.downloadInvoicePdf(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(facturaService).generatePdfFactura(1L);
    }

    @Test
    void testDownloadInvoicePdf_Error() {
        // Arrange
        when(facturaService.generatePdfFactura(1L)).thenThrow(new RuntimeException("PDF generation error"));

        // Act
        ResponseEntity<?> response = checkoutController.downloadInvoicePdf(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(facturaService).generatePdfFactura(1L);
    }
}
