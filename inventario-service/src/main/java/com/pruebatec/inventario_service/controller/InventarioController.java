package com.pruebatec.inventario_service.controller;


import com.pruebatec.inventario_service.dto.InventarioDTO;
import com.pruebatec.inventario_service.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inventarios")
@Tag(name = "Inventario", description = "API para gestionar inventarios de productos")
public class InventarioController {

    private final InventarioService inventarioService;
    
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
    
    @GetMapping("/{productoId}")
    @Operation(
        summary = "Obtener inventario por ID de producto",
        description = "Obtiene la información de inventario para un producto específico, incluyendo los datos del producto",
        responses = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado"),
            @ApiResponse(responseCode = "404", description = "Inventario o producto no encontrado"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con el servicio de productos")
        }
    )
    public ResponseEntity<?> getInventario(@PathVariable Long productoId) {
        InventarioDTO inventario = inventarioService.getInventarioByProductoId(productoId);
        return ResponseEntity.ok(formatJsonApiResponse(inventario));
    }
    
    @PatchMapping("/{productoId}")
    @Operation(
        summary = "Actualizar cantidad de un producto en inventario",
        description = "Actualiza la cantidad total disponible de un producto en el inventario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<?> actualizarInventario(
            @PathVariable Long productoId, 
            @RequestBody Map<String, Object> requestBody) {
        
        // Extraer la cantidad del cuerpo de la solicitud siguiendo el formato JSON:API
        Map<String, Object> data = (Map<String, Object>) requestBody.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        Integer cantidad = (Integer) attributes.get("cantidad");
        
        InventarioDTO inventarioActualizado = inventarioService.actualizarCantidad(productoId, cantidad);
        return ResponseEntity.ok(formatJsonApiResponse(inventarioActualizado));
    }
    
    @PostMapping("/{productoId}/compras")
    @Operation(
        summary = "Registrar compra de producto",
        description = "Registra una compra disminuyendo la cantidad disponible en inventario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Compra registrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente")
        }
    )
    public ResponseEntity<?> registrarCompra(
            @PathVariable Long productoId,
            @RequestBody Map<String, Object> requestBody) {
        
        // Extraer la cantidad comprada del cuerpo de la solicitud siguiendo el formato JSON:API
        Map<String, Object> data = (Map<String, Object>) requestBody.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        Integer cantidadComprada = (Integer) attributes.get("cantidadComprada");
        
        InventarioDTO inventarioActualizado = inventarioService.registrarCompra(productoId, cantidadComprada);
        return ResponseEntity.ok(formatJsonApiResponse(inventarioActualizado));
    }
    
    @PostMapping("/{productoId}/reposiciones")
    @Operation(
        summary = "Registrar reposición de stock",
        description = "Registra una reposición de stock aumentando la cantidad disponible en inventario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reposición registrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<?> registrarReposicion(
            @PathVariable Long productoId,
            @RequestBody Map<String, Object> requestBody) {
        
        // Extraer la cantidad repuesta del cuerpo de la solicitud siguiendo el formato JSON:API
        Map<String, Object> data = (Map<String, Object>) requestBody.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        Integer cantidadRepuesta = (Integer) attributes.get("cantidadRepuesta");
        
        InventarioDTO inventarioActualizado = inventarioService.registrarReposicion(productoId, cantidadRepuesta);
        return ResponseEntity.ok(formatJsonApiResponse(inventarioActualizado));
    }
    
    /**
     * Formatea la respuesta según el estándar JSON:API
     */
    private Map<String, Object> formatJsonApiResponse(InventarioDTO inventario) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> relationships = new HashMap<>();
        
        // Configurar atributos
        attributes.put("cantidad_restante_Inventario", inventario.getCantidad());
        
        // Configurar datos principales
        data.put("type", "inventarios");
        data.put("id", inventario.getProductoId().toString());
        data.put("attributes", attributes);
        
        // Si hay información del producto, incluirla como relación
        if (inventario.getProducto() != null) {
            Map<String, Object> productoRelationship = new HashMap<>();
            Map<String, Object> productoData = new HashMap<>();
            
            productoData.put("type", "productos");
            productoData.put("id", inventario.getProducto().getId().toString());
            
            productoRelationship.put("data", productoData);
            relationships.put("producto", productoRelationship);
            data.put("relationships", relationships);
            
            // Incluir el producto como included resource
            Map<String, Object> included = new HashMap<>();
            Map<String, Object> productoAttributes = new HashMap<>();
            
            productoAttributes.put("nombre", inventario.getProducto().getNombre());
            productoAttributes.put("precio", inventario.getProducto().getPrecio());
            
            included.put("type", "productos");
            included.put("id", inventario.getProducto().getId().toString());
            included.put("attributes", productoAttributes);
            
            response.put("included", new Object[]{included});
        }
        
        response.put("data", data);
        return response;
    }
}
