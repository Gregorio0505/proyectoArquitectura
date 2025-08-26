package com.example.pharmacy.controllers;

import com.example.pharmacy.dto.CarritoDTO;
import com.example.pharmacy.model.Carrito;
import com.example.pharmacy.model.Usuario;
import com.example.pharmacy.service.CarritoService;
import com.example.pharmacy.service.UsuarioService;
import com.example.pharmacy.repository.CarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoControllerTest {

    @Mock
    private CarritoService carritoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CarritoController carritoController;

    private Usuario testUsuario;
    private CarritoDTO testCarritoDTO;

    @BeforeEach
    void setUp() {
        testUsuario = new Usuario();
        testUsuario.setIdUsuario(1L);
        testUsuario.setCorreo("test@example.com");

        testCarritoDTO = new CarritoDTO();
        testCarritoDTO.setIdCart(1L);
        testCarritoDTO.setIdUsuario(1L);

        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(usuarioService.findByCorreo("test@example.com")).thenReturn(testUsuario);
    }

    @Test
    void testGetCart_Success() {
        // Arrange
        when(carritoService.getActiveCart(1L)).thenReturn(testCarritoDTO);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.getCart();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCarritoDTO, response.getBody());
        verify(carritoService).getActiveCart(1L);
    }

    @Test
    void testAddItem_Success() {
        // Arrange
        when(carritoService.addItem(1L, 1L, 2)).thenReturn(testCarritoDTO);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.addItem(1L, 2);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCarritoDTO, response.getBody());
        verify(carritoService).addItem(1L, 1L, 2);
    }

    @Test
    void testUpdateItemQuantity_Success() {
        // Arrange
        when(carritoService.updateItemQuantity(1L, 1L, 3)).thenReturn(testCarritoDTO);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.updateItemQuantity(1L, 3);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCarritoDTO, response.getBody());
        verify(carritoService).updateItemQuantity(1L, 1L, 3);
    }

    @Test
    void testRemoveItem_Success() {
        // Arrange
        when(carritoService.removeItem(1L, 1L)).thenReturn(testCarritoDTO);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.removeItem(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCarritoDTO, response.getBody());
        verify(carritoService).removeItem(1L, 1L);
    }

    @Test
    void testCheckout_Success() {
        // Arrange
        when(carritoService.checkout(1L)).thenReturn(testCarritoDTO);

        // Act
        ResponseEntity<?> response = carritoController.checkout();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("SUCCESS", responseBody.get("status"));
        assertEquals("Compra finalizada exitosamente", responseBody.get("message"));
        assertEquals(testCarritoDTO, responseBody.get("cart"));
        
        verify(carritoService).checkout(1L);
    }

    @Test
    void testCheckout_ValidationError() {
        // Arrange
        when(carritoService.checkout(1L)).thenThrow(new IllegalStateException("Carrito vacío"));

        // Act
        ResponseEntity<?> response = carritoController.checkout();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("ERROR", responseBody.get("status"));
        assertEquals("Carrito vacío", responseBody.get("message"));
        
        verify(carritoService).checkout(1L);
    }

    @Test
    void testCheckout_GeneralError() {
        // Arrange
        when(carritoService.checkout(1L)).thenThrow(new RuntimeException("Error interno"));

        // Act
        ResponseEntity<?> response = carritoController.checkout();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("ERROR", responseBody.get("status"));
        assertEquals("Error al procesar la compra: Error interno", responseBody.get("message"));
        
        verify(carritoService).checkout(1L);
    }


}
