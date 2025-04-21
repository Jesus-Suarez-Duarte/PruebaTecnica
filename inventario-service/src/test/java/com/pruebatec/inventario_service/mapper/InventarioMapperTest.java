package com.pruebatec.inventario_service.mapper;


import com.pruebatec.inventario_service.dto.InventarioDTO;
import com.pruebatec.inventario_service.entity.Inventario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventarioMapperTest {

    @InjectMocks
    private InventarioMapper inventarioMapper;

    @Test
    @DisplayName("Test convertir de entidad a DTO")
    void testToDto() {
        // Arrange
        Inventario inventario = new Inventario(1L, 10);
        
        // Act
        InventarioDTO result = inventarioMapper.toDTO(inventario);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductoId());
        assertEquals(10, result.getCantidad());
        assertNull(result.getProducto());
    }
    
    @Test
    @DisplayName("Test convertir de DTO a entidad")
    void testToEntity() {
        // Arrange
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(1L);
        dto.setCantidad(10);
        
        // Act
        Inventario result = inventarioMapper.toEntity(dto);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductoId());
        assertEquals(10, result.getCantidad());
    }
    
    @Test
    @DisplayName("Test convertir entidad nula a DTO")
    void testToDtoWithNull() {
        // Act
        InventarioDTO result = inventarioMapper.toDTO(null);
        
        // Assert
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test convertir DTO nulo a entidad")
    void testToEntityWithNull() {
        // Act
        Inventario result = inventarioMapper.toEntity(null);
        
        // Assert
        assertNull(result);
    }
}