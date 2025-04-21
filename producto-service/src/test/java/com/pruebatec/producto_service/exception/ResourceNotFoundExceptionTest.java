package com.pruebatec.producto_service.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceNotFoundExceptionTest {

    @Test
    public void testConstructorConMensaje() {
        // Arrange
        String mensaje = "Mensaje de error personalizado";
        
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(mensaje);
        
        // Assert
        assertEquals(mensaje, exception.getMessage());
    }
    
    @Test
    public void testConstructorConParametros() {
        // Arrange
        String resourceName = "Producto";
        String fieldName = "id";
        Object fieldValue = 1L;
        String expectedMessage = "Producto no encontrado con id: '1'";
        
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(resourceName, fieldName, fieldValue);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    public void testConstructorConParametrosString() {
        // Arrange
        String resourceName = "Usuario";
        String fieldName = "email";
        Object fieldValue = "test@example.com";
        String expectedMessage = "Usuario no encontrado con email: 'test@example.com'";
        
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(resourceName, fieldName, fieldValue);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
}
