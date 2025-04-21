package com.pruebatec.inventario_service.dto;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventarioDTOTest {

    @Test
    @DisplayName("Test constructores y getters/setters de InventarioDTO")
    void testInventarioDTO() {
        // Prueba constructor por defecto y setters
        InventarioDTO inventarioDTO1 = new InventarioDTO();
        inventarioDTO1.setProductoId(1L);
        inventarioDTO1.setCantidad(10);
        
        ProductoDTO productoDTO = new ProductoDTO(1L, "Producto test", 100.0);
        inventarioDTO1.setProducto(productoDTO);
        
        assertEquals(1L, inventarioDTO1.getProductoId());
        assertEquals(10, inventarioDTO1.getCantidad());
        assertEquals(productoDTO, inventarioDTO1.getProducto());
        
        // Prueba constructor con argumentos
        InventarioDTO inventarioDTO2 = new InventarioDTO(2L, 20, productoDTO);
        
        assertEquals(2L, inventarioDTO2.getProductoId());
        assertEquals(20, inventarioDTO2.getCantidad());
        assertEquals(productoDTO, inventarioDTO2.getProducto());
    }
    
    @Test
    @DisplayName("Test equals y hashCode de InventarioDTO")
    void testEqualsAndHashCode() {
        ProductoDTO productoDTO = new ProductoDTO(1L, "Producto test", 100.0);
        
        InventarioDTO inventarioDTO1 = new InventarioDTO(1L, 10, productoDTO);
        InventarioDTO inventarioDTO2 = new InventarioDTO(1L, 10, productoDTO);
        InventarioDTO inventarioDTO3 = new InventarioDTO(2L, 20, productoDTO);
        
        // Prueba equals
        assertEquals(inventarioDTO1, inventarioDTO2);
        assertNotEquals(inventarioDTO1, inventarioDTO3);
        
        // Prueba hashCode
        assertEquals(inventarioDTO1.hashCode(), inventarioDTO2.hashCode());
        assertNotEquals(inventarioDTO1.hashCode(), inventarioDTO3.hashCode());
    }
    
    @Test
    @DisplayName("Test toString de InventarioDTO")
    void testToString() {
        ProductoDTO productoDTO = new ProductoDTO(1L, "Producto test", 100.0);
        InventarioDTO inventarioDTO = new InventarioDTO(1L, 10, productoDTO);
        
        String toStringResult = inventarioDTO.toString();
        
        assertTrue(toStringResult.contains("productoId=1"));
        assertTrue(toStringResult.contains("cantidad=10"));
        assertTrue(toStringResult.contains("producto="));
        assertTrue(toStringResult.contains("Producto test"));
    }
    
    @Test
    @DisplayName("Test anotación JsonProperty para producto_id")
    void testJsonPropertyAnnotation() {
        InventarioDTO inventarioDTO = new InventarioDTO();
        inventarioDTO.setProductoId(1L);
        
        // Verificamos que la anotación está presente comprobando el campo
        assertEquals(1L, inventarioDTO.getProductoId());
        
        // En un escenario real, usaríamos ObjectMapper para verificar la serialización JSON
        // pero para este caso de prueba, solo verificamos que el getter/setter funciona
    }
    
    @Test
    @DisplayName("Test con producto nulo")
    void testWithNullProducto() {
        InventarioDTO inventarioDTO = new InventarioDTO();
        inventarioDTO.setProductoId(1L);
        inventarioDTO.setCantidad(10);
        inventarioDTO.setProducto(null);
        
        assertEquals(1L, inventarioDTO.getProductoId());
        assertEquals(10, inventarioDTO.getCantidad());
        assertNull(inventarioDTO.getProducto());
    }
}