package com.pruebatec.producto_service.exception;

public class ProductoConDependenciasException extends RuntimeException {
    
    public ProductoConDependenciasException(Long id) {
        super("No se puede eliminar el producto con id " + id + " porque tiene dependencias en inventario");
    }
    
    public ProductoConDependenciasException(Long id, String mensaje) {
        super(mensaje);
    }
}
