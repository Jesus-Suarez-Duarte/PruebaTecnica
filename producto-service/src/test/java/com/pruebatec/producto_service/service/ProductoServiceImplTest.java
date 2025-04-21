package com.pruebatec.producto_service.service;

import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.entity.Producto;
import com.pruebatec.producto_service.exception.ProductoConDependenciasException;
import com.pruebatec.producto_service.exception.ResourceNotFoundException;
import com.pruebatec.producto_service.mapper.ProductoMapper;
import com.pruebatec.producto_service.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;
    private ProductoDTO productoDTO;
    private ProductoDTO productoActualizadoDTO;

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
        
        
        productoActualizadoDTO = new ProductoDTO();
        productoActualizadoDTO.setId(1L);
        productoActualizadoDTO.setNombre("Producto Actualizado");
        productoActualizadoDTO.setPrecio(150.0);
    }

    @Test
    public void testCrearProducto() {
        // Arrange
        when(productoMapper.toEntity(any(ProductoDTO.class))).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // Act
        ProductoDTO resultado = productoService.crearProducto(productoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Producto Test", resultado.getNombre());
        assertEquals(100.0, resultado.getPrecio());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    public void testObtenerProductoPorId_Existe() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toDTO(producto)).thenReturn(productoDTO);

        // Act
        ProductoDTO resultado = productoService.obtenerProductoPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Producto Test", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    public void testObtenerProductoPorId_NoExiste() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.obtenerProductoPorId(99L);
        });
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    public void testActualizarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // Cambiar valores para actualización
        ProductoDTO productoActualizado = new ProductoDTO();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Producto Actualizado");
        productoActualizado.setPrecio(150.0);

        // Act
        ProductoDTO resultado = productoService.actualizarProducto(1L, productoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    public void testEliminarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).delete(producto);

        // Act
        productoService.eliminarProducto(1L);

        // Assert
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).delete(producto);
    }

    @Test
    public void testEliminarProducto_ConDependencias() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doThrow(new DataIntegrityViolationException("Foreign key constraint"))
            .when(productoRepository).flush();

        // Act & Assert
        assertThrows(ProductoConDependenciasException.class, () -> {
            productoService.eliminarProducto(1L);
        });
        
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).delete(producto);
        verify(productoRepository, times(1)).flush();
    }

    @Test
    public void testListarProductos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Producto> productos = Arrays.asList(producto);
        Page<Producto> productosPage = new PageImpl<>(productos, pageable, productos.size());

        when(productoRepository.findAll(pageable)).thenReturn(productosPage);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // Act
        Page<ProductoDTO> resultado = productoService.listarProductos(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L, resultado.getContent().get(0).getId());
        assertEquals("Producto Test", resultado.getContent().get(0).getNombre());
        verify(productoRepository, times(1)).findAll(pageable);
    }
    @Test
    public void testActualizarProducto_Exitoso() {
        // Arrange
        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Producto Actualizado");
        productoActualizado.setPrecio(BigDecimal.valueOf(150.0));
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);
        when(productoMapper.toDTO(productoActualizado)).thenReturn(productoActualizadoDTO);

        // Act
        ProductoDTO resultado = productoService.actualizarProducto(1L, productoActualizadoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Producto Actualizado", resultado.getNombre());
        assertEquals(150.0, resultado.getPrecio());
        
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    public void testActualizarProducto_ProductoNoExiste() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productoService.actualizarProducto(99L, productoActualizadoDTO);
        });
        
        assertEquals("Producto no encontrado con id: '99'", exception.getMessage());
        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    public void testActualizarProducto_PrecioNull() {
        // Arrange
        productoActualizadoDTO.setPrecio(null);
        
        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Producto Actualizado");
        productoActualizado.setPrecio(null);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);
        when(productoMapper.toDTO(productoActualizado)).thenReturn(productoActualizadoDTO);

        // Act
        ProductoDTO resultado = productoService.actualizarProducto(1L, productoActualizadoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Producto Actualizado", resultado.getNombre());
        assertNull(resultado.getPrecio());
        
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    public void testEliminarProducto_Exitoso() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).delete(producto);
        doNothing().when(productoRepository).flush();

        // Act
        productoService.eliminarProducto(1L);

        // Assert
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).delete(producto);
        verify(productoRepository, times(1)).flush();
    }

    @Test
    public void testEliminarProducto_ProductoNoExiste() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productoService.eliminarProducto(99L);
        });
        
        assertEquals("Producto no encontrado con id: '99'", exception.getMessage());
        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).delete(any(Producto.class));
        verify(productoRepository, never()).flush();
    }

}