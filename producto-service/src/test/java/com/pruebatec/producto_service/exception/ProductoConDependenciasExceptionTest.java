package com.pruebatec.producto_service.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoConDependenciasExceptionTest {

    @Test
    public void testConstructorConId() {
        // Arrange
        Long id = 1L;
        String expectedMessage = "No se puede eliminar el producto con id 1 porque tiene dependencias en inventario";
        
        // Act
        ProductoConDependenciasException exception = new ProductoConDependenciasException(id);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    public void testConstructorConIdYMensaje() {
        // Arrange
        Long id = 2L;
        String mensaje = "Mensaje personalizado para id 2";
        
        // Act
        ProductoConDependenciasException exception = new ProductoConDependenciasException(id, mensaje);
        
        // Assert
        assertEquals(mensaje, exception.getMessage());
    }
}