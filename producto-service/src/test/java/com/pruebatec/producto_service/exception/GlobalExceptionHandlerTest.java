package com.pruebatec.producto_service.exception;

import com.pruebatec.producto_service.json.JsonApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    @Mock
    private WebRequest webRequest;
    
    @BeforeEach
    public void setup() {
        // No setup required for these tests
    }
    
    @Test
    public void testResourceNotFoundException() {
        // Arrange
        String errorMessage = "Producto no encontrado con id: '1'";
        ResourceNotFoundException exception = new ResourceNotFoundException("Producto", "id", 1L);
        
        // Act
        ResponseEntity<JsonApiResponse<GlobalExceptionHandler.ErrorAttributes>> responseEntity = 
                exceptionHandler.resourceNotFoundException(exception, webRequest);
        
        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        
        JsonApiResponse<GlobalExceptionHandler.ErrorAttributes> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<GlobalExceptionHandler.ErrorAttributes> resource = response.getData().get(0);
        assertEquals("error", resource.getType());
        assertEquals("1", resource.getId());
        
        GlobalExceptionHandler.ErrorAttributes errorAttributes = resource.getAttributes();
        assertNotNull(errorAttributes);
        assertEquals(HttpStatus.NOT_FOUND.toString(), errorAttributes.getCodigo());
        assertEquals("Recurso no encontrado", errorAttributes.getTitulo());
        assertEquals(errorMessage, errorAttributes.getDetalle());
    }
    
    @Test
    public void testProductoConDependenciasException() {
        // Arrange
        Long id = 1L;
        String errorMessage = "No se puede eliminar el producto con id 1 porque tiene dependencias en inventario";
        ProductoConDependenciasException exception = new ProductoConDependenciasException(id);
        
        // Act
        ResponseEntity<JsonApiResponse<GlobalExceptionHandler.ErrorAttributes>> responseEntity = 
                exceptionHandler.productoConDependenciasException(exception, webRequest);
        
        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        
        JsonApiResponse<GlobalExceptionHandler.ErrorAttributes> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<GlobalExceptionHandler.ErrorAttributes> resource = response.getData().get(0);
        assertEquals("error", resource.getType());
        assertEquals("1", resource.getId());
        
        GlobalExceptionHandler.ErrorAttributes errorAttributes = resource.getAttributes();
        assertNotNull(errorAttributes);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorAttributes.getCodigo());
        assertEquals("No se puede eliminar el producto", errorAttributes.getTitulo());
        assertEquals(errorMessage, errorAttributes.getDetalle());
    }
    
    @Test
    public void testGlobalExceptionHandler() {
        // Arrange
        String errorMessage = "Error inesperado";
        Exception exception = new Exception(errorMessage);
        
        // Act
        ResponseEntity<JsonApiResponse<GlobalExceptionHandler.ErrorAttributes>> responseEntity = 
                exceptionHandler.globalExceptionHandler(exception, webRequest);
        
        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        
        JsonApiResponse<GlobalExceptionHandler.ErrorAttributes> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<GlobalExceptionHandler.ErrorAttributes> resource = response.getData().get(0);
        assertEquals("error", resource.getType());
        assertEquals("1", resource.getId());
        
        GlobalExceptionHandler.ErrorAttributes errorAttributes = resource.getAttributes();
        assertNotNull(errorAttributes);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorAttributes.getCodigo());
        assertEquals("Error interno del servidor", errorAttributes.getTitulo());
        assertEquals(errorMessage, errorAttributes.getDetalle());
    }
    
    @Test
    public void testErrorAttributesClass() {
        // Arrange
        String codigo = "404";
        String titulo = "Recurso no encontrado";
        String detalle = "Producto no encontrado con id: '1'";
        
        // Act
        GlobalExceptionHandler.ErrorAttributes errorAttributes1 = new GlobalExceptionHandler.ErrorAttributes(codigo, titulo, detalle);
        GlobalExceptionHandler.ErrorAttributes errorAttributes2 = new GlobalExceptionHandler.ErrorAttributes();
        errorAttributes2.setCodigo(codigo);
        errorAttributes2.setTitulo(titulo);
        errorAttributes2.setDetalle(detalle);
        
        // Assert
        assertEquals(codigo, errorAttributes1.getCodigo());
        assertEquals(titulo, errorAttributes1.getTitulo());
        assertEquals(detalle, errorAttributes1.getDetalle());
        
        assertEquals(errorAttributes1.getCodigo(), errorAttributes2.getCodigo());
        assertEquals(errorAttributes1.getTitulo(), errorAttributes2.getTitulo());
        assertEquals(errorAttributes1.getDetalle(), errorAttributes2.getDetalle());
        
        assertEquals(errorAttributes1, errorAttributes2);
        assertEquals(errorAttributes1.hashCode(), errorAttributes2.hashCode());
        
        String toString = errorAttributes1.toString();
        assertTrue(toString.contains(codigo));
        assertTrue(toString.contains(titulo));
        assertTrue(toString.contains(detalle));
    }
}