package com.pruebatec.producto_service.mapper;

import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductoMapperTest {

    @InjectMocks
    private ProductoMapper productoMapper;

    private Producto producto;
    private ProductoDTO productoDTO;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(100.0));

        productoDTO = new ProductoDTO();
        productoDTO.setId(1L);
        productoDTO.setNombre("Producto Test");
        productoDTO.setPrecio(100.0);
    }

    @Test
    public void testToDTO() {
        // Act
        ProductoDTO result = productoMapper.toDTO(producto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Producto Test", result.getNombre());
        assertEquals(100.0, result.getPrecio());
    }

    @Test
    public void testToDTO_Null() {
        // Act
        ProductoDTO result = productoMapper.toDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testToDTO_NullPrecio() {
        // Arrange
        producto.setPrecio(null);

        // Act
        ProductoDTO result = productoMapper.toDTO(producto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Producto Test", result.getNombre());
        assertNull(result.getPrecio());
    }

    @Test
    public void testToDTOList() {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Producto 2");
        producto2.setPrecio(BigDecimal.valueOf(200.0));

        List<Producto> productos = Arrays.asList(producto, producto2);

        // Act
        List<ProductoDTO> result = productoMapper.toDTOList(productos);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verificar primer producto
        assertEquals(1L, result.get(0).getId());
        assertEquals("Producto Test", result.get(0).getNombre());
        assertEquals(100.0, result.get(0).getPrecio());
        
        // Verificar segundo producto
        assertEquals(2L, result.get(1).getId());
        assertEquals("Producto 2", result.get(1).getNombre());
        assertEquals(200.0, result.get(1).getPrecio());
    }

    @Test
    public void testToDTOList_Null() {
        // Act
        List<ProductoDTO> result = productoMapper.toDTOList(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testToDTOList_Empty() {
        // Act
        List<ProductoDTO> result = productoMapper.toDTOList(Arrays.asList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToEntity() {
        // Act
        Producto result = productoMapper.toEntity(productoDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Producto Test", result.getNombre());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(result.getPrecio()));
    }

    @Test
    public void testToEntity_Null() {
        // Act
        Producto result = productoMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void testToEntity_NullPrecio() {
        // Arrange
        productoDTO.setPrecio(null);

        // Act
        Producto result = productoMapper.toEntity(productoDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Producto Test", result.getNombre());
        assertNull(result.getPrecio());
    }
}