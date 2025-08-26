package com.example.pharmacy.controllers;

import com.example.pharmacy.dto.UserDTO;
import com.example.pharmacy.model.Rol;
import com.example.pharmacy.model.Usuario;
import com.example.pharmacy.repository.RolRepository;
import com.example.pharmacy.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private AdminController adminController;

    private UserDTO testUserDTO;
    private Usuario testUsuario;
    private Rol testRol;
    private List<UserDTO> testUserList;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setCorreo("test@example.com");
        testUserDTO.setNombre("Test User");

        testUsuario = new Usuario();
        testUsuario.setIdUsuario(1L);
        testUsuario.setCorreo("test@example.com");
        testUsuario.setNombre("Test User");

        testRol = new Rol();
        testRol.setIdRol(1L);
        testRol.setNombreRol("USUARIO");

        testUserList = Arrays.asList(testUserDTO);
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        when(usuarioService.getAllUsers()).thenReturn(testUserList);

        // Act
        ResponseEntity<List<UserDTO>> response = adminController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserList, response.getBody());
        verify(usuarioService).getAllUsers();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // Arrange
        when(usuarioService.getAllUsers()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<UserDTO>> response = adminController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(usuarioService).getAllUsers();
    }

    @Test
    void testFilterUsers_Success() {
        // Arrange
        String email = "test@example.com";
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now();
        String role = "USUARIO";

        when(usuarioService.findUsersByFilters(email, fromDate, toDate, role)).thenReturn(testUserList);

        // Act
        ResponseEntity<List<UserDTO>> response = adminController.filterUsers(email, fromDate, toDate, role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserList, response.getBody());
        verify(usuarioService).findUsersByFilters(email, fromDate, toDate, role);
    }

    @Test
    void testFilterUsers_WithNullParameters() {
        // Arrange
        when(usuarioService.findUsersByFilters(null, null, null, null)).thenReturn(testUserList);

        // Act
        ResponseEntity<List<UserDTO>> response = adminController.filterUsers(null, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserList, response.getBody());
        verify(usuarioService).findUsersByFilters(null, null, null, null);
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(usuarioService.findByCorreo("1")).thenReturn(testUsuario);
        when(usuarioService.getMyProfile(testUsuario.getCorreo())).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = adminController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDTO, response.getBody());
        verify(usuarioService).findByCorreo("1");
        verify(usuarioService).getMyProfile(testUsuario.getCorreo());
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Arrange
        when(usuarioService.findByCorreo("999")).thenReturn(null);

        // Act
        ResponseEntity<UserDTO> response = adminController.getUserById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(usuarioService).findByCorreo("999");
    }

    @Test
    void testGetUserById_Exception() {
        // Arrange
        when(usuarioService.findByCorreo("1")).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<UserDTO> response = adminController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(usuarioService).findByCorreo("1");
    }

    @Test
    void testActivateUser_Success() {
        // Arrange
        Map<String, Long> activationData = new HashMap<>();
        activationData.put("roleId", 1L);

        when(usuarioService.activateUser(1L, 1L)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.activateUser(1L, activationData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User activated successfully", response.getBody().get("message"));
        verify(usuarioService).activateUser(1L, 1L);
    }

    @Test
    void testActivateUser_MissingRoleId() {
        // Arrange
        Map<String, Long> activationData = new HashMap<>();

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.activateUser(1L, activationData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Role ID is required", response.getBody().get("error"));
    }

    @Test
    void testActivateUser_ActivationFailed() {
        // Arrange
        Map<String, Long> activationData = new HashMap<>();
        activationData.put("roleId", 1L);

        when(usuarioService.activateUser(1L, 1L)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.activateUser(1L, activationData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to activate user", response.getBody().get("error"));
        verify(usuarioService).activateUser(1L, 1L);
    }

    @Test
    void testActivateUser_Exception() {
        // Arrange
        Map<String, Long> activationData = new HashMap<>();
        activationData.put("roleId", 1L);

        when(usuarioService.activateUser(1L, 1L)).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.activateUser(1L, activationData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service error", response.getBody().get("error"));
        verify(usuarioService).activateUser(1L, 1L);
    }

    @Test
    void testDeactivateUser_Success() {
        // Arrange
        when(usuarioService.deactivateUser(1L)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.deactivateUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User deactivated successfully", response.getBody().get("message"));
        verify(usuarioService).deactivateUser(1L);
    }

    @Test
    void testDeactivateUser_DeactivationFailed() {
        // Arrange
        when(usuarioService.deactivateUser(1L)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.deactivateUser(1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to deactivate user", response.getBody().get("error"));
        verify(usuarioService).deactivateUser(1L);
    }

    @Test
    void testDeactivateUser_Exception() {
        // Arrange
        when(usuarioService.deactivateUser(1L)).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.deactivateUser(1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service error", response.getBody().get("error"));
        verify(usuarioService).deactivateUser(1L);
    }
}
