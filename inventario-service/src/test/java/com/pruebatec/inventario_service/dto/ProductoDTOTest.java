package com.pruebatec.inventario_service.dto;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDTOTest {

    @Test
    @DisplayName("Test constructores y getters/setters de ProductoDTO")
    void testProductoDTO() {
        // Prueba constructor por defecto y setters
        ProductoDTO productoDTO1 = new ProductoDTO();
        productoDTO1.setId(1L);
        productoDTO1.setNombre("Producto test");
        productoDTO1.setPrecio(100.0);
        
        assertEquals(1L, productoDTO1.getId());
        assertEquals("Producto test", productoDTO1.getNombre());
        assertEquals(100.0, productoDTO1.getPrecio());
        
        // Prueba constructor con argumentos
        ProductoDTO productoDTO2 = new ProductoDTO(2L, "Otro producto", 200.0);
        
        assertEquals(2L, productoDTO2.getId());
        assertEquals("Otro producto", productoDTO2.getNombre());
        assertEquals(200.0, productoDTO2.getPrecio());
    }
    
    @Test
    @DisplayName("Test equals y hashCode de ProductoDTO")
    void testEqualsAndHashCode() {
        ProductoDTO productoDTO1 = new ProductoDTO(1L, "Producto test", 100.0);
        ProductoDTO productoDTO2 = new ProductoDTO(1L, "Producto test", 100.0);
        ProductoDTO productoDTO3 = new ProductoDTO(2L, "Otro producto", 200.0);
        
        // Prueba equals
        assertEquals(productoDTO1, productoDTO2);
        assertNotEquals(productoDTO1, productoDTO3);
        
        // Prueba hashCode
        assertEquals(productoDTO1.hashCode(), productoDTO2.hashCode());
        assertNotEquals(productoDTO1.hashCode(), productoDTO3.hashCode());
    }
    
    @Test
    @DisplayName("Test toString de ProductoDTO")
    void testToString() {
        ProductoDTO productoDTO = new ProductoDTO(1L, "Producto test", 100.0);
        String toStringResult = productoDTO.toString();
        
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("nombre=Producto test"));
        assertTrue(toStringResult.contains("precio=100.0"));
    }
}
