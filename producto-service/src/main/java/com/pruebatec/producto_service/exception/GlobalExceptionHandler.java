package com.pruebatec.producto_service.exception;

import com.pruebatec.producto_service.json.JsonApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estructura de error según JSON:API")
    static class ErrorAttributes {
        @Schema(description = "Código de error HTTP", example = "404")
        private String codigo;
        
        @Schema(description = "Título del error", example = "Recurso no encontrado")
        private String titulo;
        
        @Schema(description = "Detalle del error", example = "Producto no encontrado con id: '1'")
        private String detalle;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", 
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<JsonApiResponse<ErrorAttributes>> resourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.error("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorAttributes errorAttributes = new ErrorAttributes(
                HttpStatus.NOT_FOUND.toString(),
                "Recurso no encontrado",
                ex.getMessage()
        );
        
        // Usamos el mismo formato JsonApiResponse pero con error como atributo
        JsonApiResponse<ErrorAttributes> respuesta = new JsonApiResponse<>(
                List.of(new JsonApiResponse.Resource<>("error", "1", errorAttributes)),
                null
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ProductoConDependenciasException.class)
    @ApiResponse(responseCode = "400", description = "Producto con dependencias", 
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<JsonApiResponse<ErrorAttributes>> productoConDependenciasException(
            ProductoConDependenciasException ex, WebRequest request) {
        
        log.error("Error al eliminar producto con dependencias: {}", ex.getMessage());
        
        ErrorAttributes errorAttributes = new ErrorAttributes(
                HttpStatus.BAD_REQUEST.toString(),
                "No se puede eliminar el producto",
                ex.getMessage()
        );
        
        JsonApiResponse<ErrorAttributes> respuesta = new JsonApiResponse<>(
                List.of(new JsonApiResponse.Resource<>("error", "1", errorAttributes)),
                null
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", 
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<JsonApiResponse<ErrorAttributes>> globalExceptionHandler(
            Exception ex, WebRequest request) {
        
        log.error("Error ocurrido: {}", ex.getMessage(), ex);
        
        ErrorAttributes errorAttributes = new ErrorAttributes(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Error interno del servidor",
                ex.getMessage()
        );
        
        JsonApiResponse<ErrorAttributes> respuesta = new JsonApiResponse<>(
                List.of(new JsonApiResponse.Resource<>("error", "1", errorAttributes)),
                null
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}