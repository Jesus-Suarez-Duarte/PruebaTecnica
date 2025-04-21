package com.pruebatec.inventario_service.service;

import com.pruebatec.inventario_service.dto.InventarioDTO;
import com.pruebatec.inventario_service.dto.ProductoDTO;
import com.pruebatec.inventario_service.entity.Inventario;
import com.pruebatec.inventario_service.event.InventarioEventPublisher;
import com.pruebatec.inventario_service.exception.CommunicationException;
import com.pruebatec.inventario_service.mapper.InventarioMapper;
import com.pruebatec.inventario_service.repository.InventarioRepository;
import com.pruebatec.inventario_service.service.InventarioService;
import com.pruebatec.inventario_service.service.ProductoClientService;
import com.pruebatec.inventario_service.service.impl.InventarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private InventarioMapper inventarioMapper;

    @Mock
    private ProductoClientService productoClientService;

    @Mock
    private InventarioEventPublisher eventPublisher;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Inventario inventario;
    private InventarioDTO inventarioDTO;
    private ProductoDTO productoDTO;
    private final Long PRODUCTO_ID = 1L;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        inventario = new Inventario(PRODUCTO_ID, 10);
        
        inventarioDTO = new InventarioDTO();
        inventarioDTO.setProductoId(PRODUCTO_ID);
        inventarioDTO.setCantidad(10);
        
        productoDTO = new ProductoDTO();
        productoDTO.setId(PRODUCTO_ID);
        productoDTO.setNombre("Producto de prueba");
        productoDTO.setPrecio(100.0);
    }


     

    
    @Test
    @DisplayName("Prueba actualizarCantidad con inventario existente")
    void actualizarCantidadExistente() {
        // Arrange
        when(inventarioRepository.findByProductoId(PRODUCTO_ID)).thenReturn(Optional.of(inventario));
        
        Inventario inventarioActualizado = new Inventario(PRODUCTO_ID, 20);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);
        
        InventarioDTO inventarioDTOActualizado = new InventarioDTO();
        inventarioDTOActualizado.setProductoId(PRODUCTO_ID);
        inventarioDTOActualizado.setCantidad(20);
        
        when(inventarioMapper.toDTO(any(Inventario.class))).thenReturn(inventarioDTOActualizado);
        doNothing().when(eventPublisher).publicarCambioInventario(any(InventarioDTO.class), any(InventarioEventPublisher.TipoOperacion.class));
        
        // Act
        InventarioDTO result = inventarioService.actualizarCantidad(PRODUCTO_ID, 20);
        
        // Assert
        assertNotNull(result);
        assertEquals(PRODUCTO_ID, result.getProductoId());
        assertEquals(20, result.getCantidad());
        
        // Verify
        verify(inventarioRepository).findByProductoId(PRODUCTO_ID);
        verify(inventarioRepository).save(any(Inventario.class));
        verify(inventarioMapper).toDTO(any(Inventario.class));
        verify(eventPublisher).publicarCambioInventario(any(InventarioDTO.class), eq(InventarioEventPublisher.TipoOperacion.ACTUALIZACIÓN));
    }
    
    @Test
    @DisplayName("Prueba actualizarCantidad con cantidad negativa")
    void actualizarCantidadNegativa() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventarioService.actualizarCantidad(PRODUCTO_ID, -5);
        });
        
        // Verify
        verify(inventarioRepository, never()).save(any(Inventario.class));
        verify(eventPublisher, never()).publicarCambioInventario(any(InventarioDTO.class), any(InventarioEventPublisher.TipoOperacion.class));
    }
    
       
    @Test
    @DisplayName("Prueba registrarCompra con inventario existente y stock suficiente")
    void registrarCompraExistente() {
        // Arrange
        when(inventarioRepository.findByProductoId(PRODUCTO_ID)).thenReturn(Optional.of(inventario));
        
        Inventario inventarioActualizado = new Inventario(PRODUCTO_ID, 5);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);
        
        InventarioDTO inventarioDTOActualizado = new InventarioDTO();
        inventarioDTOActualizado.setProductoId(PRODUCTO_ID);
        inventarioDTOActualizado.setCantidad(5);
        
        when(inventarioMapper.toDTO(any(Inventario.class))).thenReturn(inventarioDTOActualizado);
        
        // Act
        InventarioDTO result = inventarioService.registrarCompra(PRODUCTO_ID, 5);
        
        // Assert
        assertNotNull(result);
        assertEquals(PRODUCTO_ID, result.getProductoId());
        assertEquals(5, result.getCantidad());
        
        // Verify
        verify(inventarioRepository).findByProductoId(PRODUCTO_ID);
        verify(inventarioRepository).save(any(Inventario.class));
        verify(inventarioMapper).toDTO(any(Inventario.class));
        verify(eventPublisher).publicarCambioInventario(any(InventarioDTO.class), eq(InventarioEventPublisher.TipoOperacion.COMPRA));
    }
    
    @Test
    @DisplayName("Prueba registrarCompra con cantidad no positiva")
    void registrarCompraNoPositiva() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventarioService.registrarCompra(PRODUCTO_ID, 0);
        });
        
        // Verify
        verify(inventarioRepository, never()).save(any(Inventario.class));
        verify(eventPublisher, never()).publicarCambioInventario(any(InventarioDTO.class), any(InventarioEventPublisher.TipoOperacion.class));
    }
    
    @Test
    @DisplayName("Prueba registrarCompra con stock insuficiente")
    void registrarCompraStockInsuficiente() {
        // Arrange
        when(inventarioRepository.findByProductoId(PRODUCTO_ID)).thenReturn(Optional.of(inventario));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventarioService.registrarCompra(PRODUCTO_ID, 15);
        });
        
        // Verify
        verify(inventarioRepository).findByProductoId(PRODUCTO_ID);
        verify(inventarioRepository, never()).save(any(Inventario.class));
        verify(eventPublisher, never()).publicarCambioInventario(any(InventarioDTO.class), any(InventarioEventPublisher.TipoOperacion.class));
    }
    
    @Test
    @DisplayName("Prueba registrarReposicion con inventario existente")
    void registrarReposicionExistente() {
        // Arrange
        when(inventarioRepository.findByProductoId(PRODUCTO_ID)).thenReturn(Optional.of(inventario));
        
        Inventario inventarioActualizado = new Inventario(PRODUCTO_ID, 15);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);
        
        InventarioDTO inventarioDTOActualizado = new InventarioDTO();
        inventarioDTOActualizado.setProductoId(PRODUCTO_ID);
        inventarioDTOActualizado.setCantidad(15);
        
        when(inventarioMapper.toDTO(any(Inventario.class))).thenReturn(inventarioDTOActualizado);
        
        // Act
        InventarioDTO result = inventarioService.registrarReposicion(PRODUCTO_ID, 5);
        
        // Assert
        assertNotNull(result);
        assertEquals(PRODUCTO_ID, result.getProductoId());
        assertEquals(15, result.getCantidad());
        
        // Verify
        verify(inventarioRepository).findByProductoId(PRODUCTO_ID);
        verify(inventarioRepository).save(any(Inventario.class));
        verify(inventarioMapper).toDTO(any(Inventario.class));
        verify(eventPublisher).publicarCambioInventario(any(InventarioDTO.class), eq(InventarioEventPublisher.TipoOperacion.REPOSICIÓN));
    }
    
    @Test
    @DisplayName("Prueba registrarReposicion con cantidad no positiva")
    void registrarReposicionNoPositiva() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventarioService.registrarReposicion(PRODUCTO_ID, 0);
        });
        
        // Verify
        verify(inventarioRepository, never()).save(any(Inventario.class));
        verify(eventPublisher, never()).publicarCambioInventario(any(InventarioDTO.class), any(InventarioEventPublisher.TipoOperacion.class));
    }
    

    
 
}