package com.pruebatec.inventario_service.service;

import com.pruebatec.inventario_service.dto.ProductoDTO;

/**
 * Interfaz para el cliente que comunica con el servicio de productos
 */
public interface ProductoClientService {
    
    /**
     * Obtiene un producto por su ID
     * @param id ID del producto a buscar
     * @return ProductoDTO con la información del producto
     */
    ProductoDTO getProductoById(Long id);
    
    /**
     * Clase interna para manejar la respuesta JSON API
     */
    class ProductoResponse {
        private ProductoData data;
        
        public ProductoData getData() {
            return data;
        }
        
        public void setData(ProductoData data) {
            this.data = data;
        }
    }
    
    class ProductoData {
        private String type;
        private String id;
        private ProductoAttributes attributes;
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public ProductoAttributes getAttributes() {
            return attributes;
        }
        
        public void setAttributes(ProductoAttributes attributes) {
            this.attributes = attributes;
        }
    }
    
    class ProductoAttributes {
        private String nombre;
        private Double precio;
        
        public String getNombre() {
            return nombre;
        }
        
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
        
        public Double getPrecio() {
            return precio;
        }
        
        public void setPrecio(Double precio) {
            this.precio = precio;
        }
    }
}