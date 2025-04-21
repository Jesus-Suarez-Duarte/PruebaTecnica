package com.pruebatec.inventario_service.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        
        error.put("status", "404");
        error.put("title", "Recurso no encontrado");
        error.put("detail", ex.getMessage());
        
        errors.put("errors", new Object[]{error});
        
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(CommunicationException.class)
    public ResponseEntity<?> handleCommunicationException(CommunicationException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        
        error.put("status", "503");
        error.put("title", "Error de comunicación");
        error.put("detail", ex.getMessage());
        
        errors.put("errors", new Object[]{error});
        
        return new ResponseEntity<>(errors, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<?> handleRestClientException(RestClientException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        
        error.put("status", "503");
        error.put("title", "Error al comunicarse con el servicio externo");
        error.put("detail", "No se pudo procesar la respuesta del servicio: " + ex.getMessage());
        
        errors.put("errors", new Object[]{error});
        
        return new ResponseEntity<>(errors, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        
        error.put("status", "400");
        error.put("title", "Parámetros inválidos");
        error.put("detail", ex.getMessage());
        
        errors.put("errors", new Object[]{error});
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        
        error.put("status", "500");
        error.put("title", "Error interno del servidor");
        error.put("detail", ex.getMessage());
        
        errors.put("errors", new Object[]{error});
        
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> error = new HashMap<>();
        
        // Verificar si es una violación de clave foránea de FK_INVENTARIO_PRODUCTO
        if (ex.getCause() instanceof ConstraintViolationException) {
            String message = ex.getMostSpecificCause().getMessage();
            if (message != null && message.contains("FK_INVENTARIO_PRODUCTO")) {
                error.put("status", "404");
                error.put("title", "Producto no encontrado");
                error.put("detail", "El producto especificado no existe en el sistema");
                
                errors.put("errors", new Object[]{error});
                
                return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
            }
        }
        
        // Para otros errores de integridad
        error.put("status", "400");
        error.put("title", "Error de integridad de datos");
        error.put("detail", "Error en los datos: " + ex.getMostSpecificCause().getMessage());
        
        errors.put("errors", new Object[]{error});
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}