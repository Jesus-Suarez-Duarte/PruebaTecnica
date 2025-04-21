package com.pruebatec.producto_service.json;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.entity.Producto;
import com.pruebatec.producto_service.json.JsonApiResponse.Resource;
import com.pruebatec.producto_service.mapper.ProductoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JsonApiResponseTest {

    @Mock
    private ProductoMapper productoMapper;

    private Producto producto;
    private ProductoDTO productoDTO;
    private ObjectMapper objectMapper;

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

        objectMapper = new ObjectMapper();
    }

    @Test
    public void testConstructorVacio() {
        // Act
        JsonApiResponse<ProductoDTO> response = new JsonApiResponse<>();
        
        // Assert
        assertNotNull(response);
        assertNull(response.getData());
        assertNull(response.getMeta());
    }

    @Test
    public void testConstructorConParametros() {
        // Arrange
        List<Resource<ProductoDTO>> data = new ArrayList<>();
        data.add(new Resource<>("productos", "1", productoDTO));
        JsonApiResponse.Meta meta = new JsonApiResponse.Meta(1, 1, 0, 10);
        
        // Act
        JsonApiResponse<ProductoDTO> response = new JsonApiResponse<>(data, meta);
        
        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("productos", response.getData().get(0).getType());
        assertEquals("1", response.getData().get(0).getId());
        assertEquals(productoDTO, response.getData().get(0).getAttributes());
        assertEquals(1, response.getMeta().getTotalElements());
        assertEquals(1, response.getMeta().getTotalPages());
        assertEquals(0, response.getMeta().getNumber());
        assertEquals(10, response.getMeta().getSize());
    }

    @Test
    public void testResourceConstructorVacio() {
        // Act
        Resource<ProductoDTO> resource = new Resource<>();
        
        // Assert
        assertNotNull(resource);
        assertNull(resource.getType());
        assertNull(resource.getId());
        assertNull(resource.getAttributes());
    }

    @Test
    public void testResourceConstructorConParametros() {
        // Act
        Resource<ProductoDTO> resource = new Resource<>("productos", "1", productoDTO);
        
        // Assert
        assertNotNull(resource);
        assertEquals("productos", resource.getType());
        assertEquals("1", resource.getId());
        assertEquals(productoDTO, resource.getAttributes());
    }

    @Test
    public void testMetaConstructorVacio() {
        // Act
        JsonApiResponse.Meta meta = new JsonApiResponse.Meta();
        
        // Assert
        assertNotNull(meta);
        assertEquals(0, meta.getTotalElements());
        assertEquals(0, meta.getTotalPages());
        assertEquals(0, meta.getNumber());
        assertEquals(0, meta.getSize());
    }

    @Test
    public void testMetaConstructorConParametros() {
        // Act
        JsonApiResponse.Meta meta = new JsonApiResponse.Meta(100, 10, 2, 10);
        
        // Assert
        assertNotNull(meta);
        assertEquals(100, meta.getTotalElements());
        assertEquals(10, meta.getTotalPages());
        assertEquals(2, meta.getNumber());
        assertEquals(10, meta.getSize());
    }

    @Test
    public void testDesdeEntidad() {
        // Arrange
        Function<Producto, ProductoDTO> mapeoAtributos = p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio().doubleValue());
            return dto;
        };
        Function<Producto, String> mapeoId = p -> p.getId().toString();
        String tipoRecurso = "productos";
        
        // Act
        JsonApiResponse<ProductoDTO> response = JsonApiResponse.desdeEntidad(
                producto, 
                mapeoAtributos, 
                mapeoId, 
                tipoRecurso
        );
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("productos", response.getData().get(0).getType());
        assertEquals("1", response.getData().get(0).getId());
        assertNotNull(response.getData().get(0).getAttributes());
        assertEquals("Producto Test", response.getData().get(0).getAttributes().getNombre());
        assertEquals(100.0, response.getData().get(0).getAttributes().getPrecio());
        assertNull(response.getMeta());
    }

    @Test
    public void testDesdePagina() {
        // Arrange
        List<Producto> productos = Arrays.asList(producto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> pagina = new PageImpl<>(productos, pageable, productos.size());
        
        Function<Producto, ProductoDTO> mapeoAtributos = p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio().doubleValue());
            return dto;
        };
        Function<Producto, String> mapeoId = p -> p.getId().toString();
        String tipoRecurso = "productos";
        
        // Act
        JsonApiResponse<ProductoDTO> response = JsonApiResponse.desdePagina(
                pagina, 
                mapeoAtributos, 
                mapeoId, 
                tipoRecurso
        );
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("productos", response.getData().get(0).getType());
        assertEquals("1", response.getData().get(0).getId());
        assertNotNull(response.getData().get(0).getAttributes());
        assertEquals("Producto Test", response.getData().get(0).getAttributes().getNombre());
        assertEquals(100.0, response.getData().get(0).getAttributes().getPrecio());
        
        assertNotNull(response.getMeta());
        assertEquals(1, response.getMeta().getTotalElements());
        assertEquals(1, response.getMeta().getTotalPages());
        assertEquals(0, response.getMeta().getNumber());
        assertEquals(10, response.getMeta().getSize());
    }

    @Test
    public void testDesdePaginaVacia() {
        // Arrange
        List<Producto> productos = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> paginaVacia = new PageImpl<>(productos, pageable, 0);
        
        Function<Producto, ProductoDTO> mapeoAtributos = p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio().doubleValue());
            return dto;
        };
        Function<Producto, String> mapeoId = p -> p.getId().toString();
        String tipoRecurso = "productos";
        
        // Act
        JsonApiResponse<ProductoDTO> response = JsonApiResponse.desdePagina(
                paginaVacia, 
                mapeoAtributos, 
                mapeoId, 
                tipoRecurso
        );
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
        
        assertNotNull(response.getMeta());
        assertEquals(0, response.getMeta().getTotalElements());
        assertEquals(0, response.getMeta().getTotalPages());
        assertEquals(0, response.getMeta().getNumber());
        assertEquals(10, response.getMeta().getSize());
    }

    @Test
    public void testDesdePaginaMultiplesElementos() {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Producto 2");
        producto2.setPrecio(BigDecimal.valueOf(200.0));
        
        List<Producto> productos = Arrays.asList(producto, producto2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> pagina = new PageImpl<>(productos, pageable, productos.size());
        
        Function<Producto, ProductoDTO> mapeoAtributos = p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio().doubleValue());
            return dto;
        };
        Function<Producto, String> mapeoId = p -> p.getId().toString();
        String tipoRecurso = "productos";
        
        // Act
        JsonApiResponse<ProductoDTO> response = JsonApiResponse.desdePagina(
                pagina, 
                mapeoAtributos, 
                mapeoId, 
                tipoRecurso
        );
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        
        // Verificar primer producto
        assertEquals("productos", response.getData().get(0).getType());
        assertEquals("1", response.getData().get(0).getId());
        assertEquals("Producto Test", response.getData().get(0).getAttributes().getNombre());
        assertEquals(100.0, response.getData().get(0).getAttributes().getPrecio());
        
        // Verificar segundo producto
        assertEquals("productos", response.getData().get(1).getType());
        assertEquals("2", response.getData().get(1).getId());
        assertEquals("Producto 2", response.getData().get(1).getAttributes().getNombre());
        assertEquals(200.0, response.getData().get(1).getAttributes().getPrecio());
        
        // Verificar metadata
        assertNotNull(response.getMeta());
        assertEquals(2, response.getMeta().getTotalElements());
        assertEquals(1, response.getMeta().getTotalPages());
        assertEquals(0, response.getMeta().getNumber());
        assertEquals(10, response.getMeta().getSize());
    }

    @Test
    public void testEqualsYHashCode() {
        // Arrange
        List<Resource<ProductoDTO>> data1 = new ArrayList<>();
        data1.add(new Resource<>("productos", "1", productoDTO));
        JsonApiResponse.Meta meta1 = new JsonApiResponse.Meta(1, 1, 0, 10);
        
        List<Resource<ProductoDTO>> data2 = new ArrayList<>();
        data2.add(new Resource<>("productos", "1", productoDTO));
        JsonApiResponse.Meta meta2 = new JsonApiResponse.Meta(1, 1, 0, 10);
        
        JsonApiResponse<ProductoDTO> response1 = new JsonApiResponse<>(data1, meta1);
        JsonApiResponse<ProductoDTO> response2 = new JsonApiResponse<>(data2, meta2);
        
        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        List<Resource<ProductoDTO>> data = new ArrayList<>();
        data.add(new Resource<>("productos", "1", productoDTO));
        JsonApiResponse.Meta meta = new JsonApiResponse.Meta(1, 1, 0, 10);
        
        JsonApiResponse<ProductoDTO> response = new JsonApiResponse<>(data, meta);
        
        // Act
        String toString = response.toString();
        
        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("data"));
        assertTrue(toString.contains("meta"));
    }
}
