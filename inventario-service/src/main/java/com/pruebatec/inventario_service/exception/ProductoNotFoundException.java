package com.pruebatec.inventario_service.exception;

public class ProductoNotFoundException extends ResourceNotFoundException {
    
    public ProductoNotFoundException(Long productoId) {
        super(String.format("El producto con ID %d no existe", productoId));
    }
}
