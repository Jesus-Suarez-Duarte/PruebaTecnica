package com.pruebatec.producto_service.controller;

import com.pruebatec.producto_service.dto.ProductoAtributosDTO;
import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.json.JsonApiResponse;
import com.pruebatec.producto_service.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Productos", description = "API para la gestión de productos")
public class ProductoController {

    private final ProductoService productoService;
    private static final String TIPO_RECURSO = "productos";
    
    // Funciones de mapeo reutilizables
    private static final Function<ProductoDTO, ProductoAtributosDTO> MAPEO_ATRIBUTOS = 
            dto -> new ProductoAtributosDTO(dto.getNombre(), dto.getPrecio());
    
    private static final Function<ProductoDTO, String> MAPEO_ID = 
            dto -> dto.getId().toString();
    
    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos del producto inválidos")
    })
    public ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> crearProducto(
            @Parameter(description = "Datos del producto a crear", required = true)
            @RequestBody ProductoDTO productoDTO) {
        log.info("Solicitud para crear producto: {}", productoDTO.getNombre());
        ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);
        
        JsonApiResponse<ProductoAtributosDTO> respuesta = JsonApiResponse.desdeEntidad(
                nuevoProducto,
                MAPEO_ATRIBUTOS,
                MAPEO_ID,
                TIPO_RECURSO
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID", description = "Devuelve un producto según su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> obtenerProductoPorId(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id) {
        log.info("Solicitud para obtener producto con id: {}", id);
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        
        JsonApiResponse<ProductoAtributosDTO> respuesta = JsonApiResponse.desdeEntidad(
                producto,
                MAPEO_ATRIBUTOS,
                MAPEO_ID,
                TIPO_RECURSO
        );
        
        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene una lista paginada de productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    })
    public ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> listarProductos(
            @Parameter(description = "Parámetros de paginación")
            Pageable pageable) {
        log.info("Solicitud para listar productos con paginación");
        Page<ProductoDTO> productos = productoService.listarProductos(pageable);
        
        JsonApiResponse<ProductoAtributosDTO> respuesta = JsonApiResponse.desdePagina(
                productos,
                MAPEO_ATRIBUTOS,
                MAPEO_ID,
                TIPO_RECURSO
        );
        
        return ResponseEntity.ok(respuesta);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos del producto inválidos")
    })
    public ResponseEntity<JsonApiResponse<ProductoAtributosDTO>> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del producto", required = true)
            @RequestBody ProductoDTO productoDTO) {
        log.info("Solicitud para actualizar producto con id: {}", id);
        ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);
        
        JsonApiResponse<ProductoAtributosDTO> respuesta = JsonApiResponse.desdeEntidad(
                productoActualizado,
                MAPEO_ATRIBUTOS,
                MAPEO_ID,
                TIPO_RECURSO
        );
        
        return ResponseEntity.ok(respuesta);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar porque tiene dependencias")
    })
    public ResponseEntity<JsonApiResponse<Map<String, String>>> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable Long id) {
        log.info("Solicitud para eliminar producto con id: {}", id);
        productoService.eliminarProducto(id);
        
        // Crear un mapa con el mensaje
        Map<String, String> mensaje = new HashMap<>();
        mensaje.put("mensaje", "Producto eliminado correctamente");
        
        // Crear un recurso con el mensaje
        JsonApiResponse.Resource<Map<String, String>> recurso = 
                new JsonApiResponse.Resource<>("mensaje", id.toString(), mensaje);
        
        // Crear la respuesta
        JsonApiResponse<Map<String, String>> respuesta = 
                new JsonApiResponse<>(List.of(recurso), null);
        
        return ResponseEntity.ok(respuesta);
    }
}