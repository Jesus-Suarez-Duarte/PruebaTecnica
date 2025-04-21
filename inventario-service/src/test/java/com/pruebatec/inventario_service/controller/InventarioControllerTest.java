package com.pruebatec.inventario_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebatec.inventario_service.dto.InventarioDTO;
import com.pruebatec.inventario_service.dto.ProductoDTO;
import com.pruebatec.inventario_service.exception.CommunicationException;
import com.pruebatec.inventario_service.exception.ResourceNotFoundException;
import com.pruebatec.inventario_service.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventarioService inventarioService;

    private InventarioDTO inventarioDTO;
    private ProductoDTO productoDTO;
    private final Long PRODUCTO_ID = 1L;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        productoDTO = new ProductoDTO();
        productoDTO.setId(PRODUCTO_ID);
        productoDTO.setNombre("Producto de prueba");
        productoDTO.setPrecio(100.0);

        inventarioDTO = new InventarioDTO();
        inventarioDTO.setProductoId(PRODUCTO_ID);
        inventarioDTO.setCantidad(10);
        inventarioDTO.setProducto(productoDTO);
    }

    @Test
    @DisplayName("GET /api/inventarios/{productoId} - Éxito")
    void getInventarioSuccess() throws Exception {
        // Arrange
        when(inventarioService.getInventarioByProductoId(PRODUCTO_ID)).thenReturn(inventarioDTO);

        // Act & Assert
        mockMvc.perform(get("/api/inventarios/{productoId}", PRODUCTO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("inventarios"))
                .andExpect(jsonPath("$.data.id").value(PRODUCTO_ID.toString()))
                .andExpect(jsonPath("$.data.attributes.cantidad_restante_Inventario").value(10))
                .andExpect(jsonPath("$.data.relationships.producto.data.type").value("productos"))
                .andExpect(jsonPath("$.data.relationships.producto.data.id").value(PRODUCTO_ID.toString()))
                .andExpect(jsonPath("$.included[0].type").value("productos"))
                .andExpect(jsonPath("$.included[0].id").value(PRODUCTO_ID.toString()))
                .andExpect(jsonPath("$.included[0].attributes.nombre").value("Producto de prueba"))
                .andExpect(jsonPath("$.included[0].attributes.precio").value(100.0));
    }

    @Test
    @DisplayName("GET /api/inventarios/{productoId} - Producto no encontrado")
    void getInventarioProductNotFound() throws Exception {
        // Arrange
        when(inventarioService.getInventarioByProductoId(anyLong()))
                .thenThrow(new ResourceNotFoundException("Producto", "id", PRODUCTO_ID));

        // Act & Assert
        mockMvc.perform(get("/api/inventarios/{productoId}", PRODUCTO_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/inventarios/{productoId} - Error de comunicación")
    void getInventarioCommunicationError() throws Exception {
        // Arrange
        when(inventarioService.getInventarioByProductoId(anyLong()))
                .thenThrow(new CommunicationException("Error de comunicación con el servicio de productos", new RuntimeException()));

        // Act & Assert
        mockMvc.perform(get("/api/inventarios/{productoId}", PRODUCTO_ID))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("PATCH /api/inventarios/{productoId} - Éxito")
    void actualizarInventarioSuccess() throws Exception {
        // Arrange
        InventarioDTO inventarioActualizado = new InventarioDTO();
        inventarioActualizado.setProductoId(PRODUCTO_ID);
        inventarioActualizado.setCantidad(20);
        inventarioActualizado.setProducto(productoDTO);

        when(inventarioService.actualizarCantidad(anyLong(), anyInt())).thenReturn(inventarioActualizado);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("cantidad", 20);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "inventarios");
        data.put("id", PRODUCTO_ID.toString());
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        // Act & Assert
        mockMvc.perform(patch("/api/inventarios/{productoId}", PRODUCTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.cantidad_restante_Inventario").value(20));
    }

    @Test
    @DisplayName("PATCH /api/inventarios/{productoId} - Cantidad negativa")
    void actualizarInventarioNegativeQuantity() throws Exception {
        // Arrange
        when(inventarioService.actualizarCantidad(anyLong(), anyInt()))
                .thenThrow(new IllegalArgumentException("La cantidad no puede ser negativa"));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("cantidad", -5);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "inventarios");
        data.put("id", PRODUCTO_ID.toString());
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        // Act & Assert
        mockMvc.perform(patch("/api/inventarios/{productoId}", PRODUCTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/inventarios/{productoId}/compras - Éxito")
    void registrarCompraSuccess() throws Exception {
        // Arrange
        InventarioDTO inventarioActualizado = new InventarioDTO();
        inventarioActualizado.setProductoId(PRODUCTO_ID);
        inventarioActualizado.setCantidad(5);
        inventarioActualizado.setProducto(productoDTO);

        when(inventarioService.registrarCompra(anyLong(), anyInt())).thenReturn(inventarioActualizado);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("cantidadComprada", 5);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "compras");
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        // Act & Assert
        mockMvc.perform(post("/api/inventarios/{productoId}/compras", PRODUCTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.cantidad_restante_Inventario").value(5));
    }

    @Test
    @DisplayName("POST /api/inventarios/{productoId}/compras - Stock insuficiente")
    void registrarCompraInsufficientStock() throws Exception {
        // Arrange
        when(inventarioService.registrarCompra(anyLong(), anyInt()))
                .thenThrow(new IllegalArgumentException("Stock insuficiente"));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("cantidadComprada", 20);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "compras");
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        // Act & Assert
        mockMvc.perform(post("/api/inventarios/{productoId}/compras", PRODUCTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/inventarios/{productoId}/reposiciones - Éxito")
    void registrarReposicionSuccess() throws Exception {
        // Arrange
        InventarioDTO inventarioActualizado = new InventarioDTO();
        inventarioActualizado.setProductoId(PRODUCTO_ID);
        inventarioActualizado.setCantidad(15);
        inventarioActualizado.setProducto(productoDTO);

        when(inventarioService.registrarReposicion(anyLong(), anyInt())).thenReturn(inventarioActualizado);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("cantidadRepuesta", 5);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "reposiciones");
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        // Act & Assert
        mockMvc.perform(post("/api/inventarios/{productoId}/reposiciones", PRODUCTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.cantidad_restante_Inventario").value(15));
    }

    @Test
    @DisplayName("POST /api/inventarios/{productoId}/reposiciones - Cantidad no positiva")
    void registrarReposicionNonPositiveQuantity() throws Exception {
        // Arrange
        when(inventarioService.registrarReposicion(anyLong(), anyInt()))
                .thenThrow(new IllegalArgumentException("La cantidad repuesta debe ser mayor a cero"));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("cantidadRepuesta", 0);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "reposiciones");
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        // Act & Assert
        mockMvc.perform(post("/api/inventarios/{productoId}/reposiciones", PRODUCTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }
}