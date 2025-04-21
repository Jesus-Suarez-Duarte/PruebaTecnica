package com.pruebatec.producto_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebatec.producto_service.dto.ProductoAtributosDTO;
import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.json.JsonApiResponse;
import com.pruebatec.producto_service.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ProductoDTO productoDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        productoDTO = new ProductoDTO();
        productoDTO.setId(1L);
        productoDTO.setNombre("Producto Test");
        productoDTO.setPrecio(100.0);

        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCrearProducto() {
        // Arrange
        when(productoService.crearProducto(any(ProductoDTO.class))).thenReturn(productoDTO);

        // Act
        ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> responseEntity = 
                productoController.crearProducto(productoDTO);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        
        JsonApiResponse<ProductoAtributosDTO> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<ProductoAtributosDTO> resource = response.getData().get(0);
        assertEquals("productos", resource.getType());
        assertEquals("1", resource.getId());
        assertEquals("Producto Test", resource.getAttributes().getNombre());
        assertEquals(100.0, resource.getAttributes().getPrecio());
        
        verify(productoService, times(1)).crearProducto(any(ProductoDTO.class));
    }

    @Test
    public void testObtenerProductoPorId() {
        // Arrange
        when(productoService.obtenerProductoPorId(1L)).thenReturn(productoDTO);

        // Act
        ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> responseEntity = 
                productoController.obtenerProductoPorId(1L);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        JsonApiResponse<ProductoAtributosDTO> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<ProductoAtributosDTO> resource = response.getData().get(0);
        assertEquals("productos", resource.getType());
        assertEquals("1", resource.getId());
        assertEquals("Producto Test", resource.getAttributes().getNombre());
        assertEquals(100.0, resource.getAttributes().getPrecio());
        
        verify(productoService, times(1)).obtenerProductoPorId(1L);
    }

    @Test
    public void testListarProductos() {
        // Arrange
        ProductoDTO producto2 = new ProductoDTO();
        producto2.setId(2L);
        producto2.setNombre("Producto 2");
        producto2.setPrecio(200.0);
        
        List<ProductoDTO> productos = Arrays.asList(productoDTO, producto2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductoDTO> productosPage = new PageImpl<>(productos, pageable, productos.size());
        
        when(productoService.listarProductos(any(Pageable.class))).thenReturn(productosPage);

        // Act
        ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> responseEntity = 
                productoController.listarProductos(pageable);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        JsonApiResponse<ProductoAtributosDTO> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        
        // Verificar primer producto
        JsonApiResponse.Resource<ProductoAtributosDTO> resource1 = response.getData().get(0);
        assertEquals("productos", resource1.getType());
        assertEquals("1", resource1.getId());
        assertEquals("Producto Test", resource1.getAttributes().getNombre());
        assertEquals(100.0, resource1.getAttributes().getPrecio());
        
        // Verificar segundo producto
        JsonApiResponse.Resource<ProductoAtributosDTO> resource2 = response.getData().get(1);
        assertEquals("productos", resource2.getType());
        assertEquals("2", resource2.getId());
        assertEquals("Producto 2", resource2.getAttributes().getNombre());
        assertEquals(200.0, resource2.getAttributes().getPrecio());
        
        // Verificar metadata
        assertNotNull(response.getMeta());
        assertEquals(2, response.getMeta().getTotalElements());
        assertEquals(1, response.getMeta().getTotalPages());
        assertEquals(0, response.getMeta().getNumber());
        assertEquals(10, response.getMeta().getSize());
        
        verify(productoService, times(1)).listarProductos(any(Pageable.class));
    }

    @Test
    public void testActualizarProducto() {
        // Arrange
        ProductoDTO productoActualizado = new ProductoDTO();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Producto Actualizado");
        productoActualizado.setPrecio(150.0);
        
        when(productoService.actualizarProducto(eq(1L), any(ProductoDTO.class)))
                .thenReturn(productoActualizado);

        // Act
        ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> responseEntity = 
                productoController.actualizarProducto(1L, productoDTO);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        JsonApiResponse<ProductoAtributosDTO> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<ProductoAtributosDTO> resource = response.getData().get(0);
        assertEquals("productos", resource.getType());
        assertEquals("1", resource.getId());
        assertEquals("Producto Actualizado", resource.getAttributes().getNombre());
        assertEquals(150.0, resource.getAttributes().getPrecio());
        
        verify(productoService, times(1)).actualizarProducto(eq(1L), any(ProductoDTO.class));
    }

    @Test
    public void testEliminarProducto() {
        // Arrange
        doNothing().when(productoService).eliminarProducto(1L);

        // Act
        ResponseEntity<JsonApiResponse<Map<String, String>>> responseEntity = 
                productoController.eliminarProducto(1L);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        JsonApiResponse<Map<String, String>> response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        
        JsonApiResponse.Resource<Map<String, String>> resource = response.getData().get(0);
        assertEquals("mensaje", resource.getType());
        assertEquals("1", resource.getId());
        assertEquals("Producto eliminado correctamente", resource.getAttributes().get("mensaje"));
        
        verify(productoService, times(1)).eliminarProducto(1L);
    }
}