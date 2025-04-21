package com.pruebatec.inventario_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebatec.inventario_service.dto.ProductoDTO;
import com.pruebatec.inventario_service.exception.CommunicationException;
import com.pruebatec.inventario_service.exception.ResourceNotFoundException;
import com.pruebatec.inventario_service.service.ProductoClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductoClientServiceImpl implements ProductoClientService {

    private static final Logger log = LoggerFactory.getLogger(ProductoClientServiceImpl.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${producto.service.url}")
    private String productoServiceUrl;
    
    public ProductoClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public ProductoDTO getProductoById(Long id) {
        try {
            log.info("Obteniendo producto con ID: {}", id);
            // Construir URL para obtener un producto específico
            String url = productoServiceUrl + "/api/productos/" + id;
            
            // Para depuración
            log.info("Realizando petición a: {}", url);
            
            // Obtener la respuesta como String para procesarla manualmente
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            String jsonResponse = responseEntity.getBody();
            
            log.info("Respuesta recibida: {}", jsonResponse);
            
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                log.error("La respuesta del servicio de productos está vacía");
                throw new ResourceNotFoundException("Producto", "id", id);
            }
            
            // Deserializar el JSON
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode dataNode = rootNode.get("data");
            
            // Verificar si data existe
            if (dataNode == null) {
                log.error("La respuesta no contiene el nodo 'data'");
                throw new ResourceNotFoundException("Producto", "id", id);
            }
            
            // Procesar el nodo data
            JsonNode productoNode = null;
            
            // Si data es un array, buscar el producto con el ID correcto
            if (dataNode.isArray()) {
                for (JsonNode node : dataNode) {
                    if (node.has("id") && node.get("id").asText().equals(id.toString())) {
                        productoNode = node;
                        break;
                    }
                }
            } else {
                // Si data es un objeto individual, verificar si es el ID correcto
                if (dataNode.has("id") && dataNode.get("id").asText().equals(id.toString())) {
                    productoNode = dataNode;
                }
            }
            
            // Si no se encontró el producto
            if (productoNode == null) {
                log.error("No se encontró el producto con ID: {}", id);
                throw new ResourceNotFoundException("Producto", "id", id);
            }
            
            // Verificar si tiene atributos
            JsonNode attributesNode = productoNode.get("attributes");
            if (attributesNode == null) {
                log.error("El producto no tiene atributos definidos");
                throw new ResourceNotFoundException("Producto", "id", id);
            }
            
            // Extraer datos del producto
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(Long.parseLong(productoNode.get("id").asText()));
            
            // Extraer nombre y precio de attributes
            if (attributesNode.has("nombre")) {
                productoDTO.setNombre(attributesNode.get("nombre").asText());
            } else {
                productoDTO.setNombre("Producto sin nombre");
            }
            
            if (attributesNode.has("precio")) {
                productoDTO.setPrecio(attributesNode.get("precio").asDouble());
            } else {
                productoDTO.setPrecio(0.0);
            }
            
            return productoDTO;
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("Producto no encontrado con ID: {}", id);
                throw new ResourceNotFoundException("Producto", "id", id);
            } else {
                log.error("Error en la petición al servicio de productos: {} - {}", e.getStatusCode(), e.getMessage());
                throw new CommunicationException("Error en la petición al servicio de productos: " + e.getMessage(), e);
            }
        } catch (HttpServerErrorException e) {
            log.error("Error en el servidor de productos: {} - {}", e.getStatusCode(), e.getMessage());
            throw new CommunicationException("Error en el servidor de productos: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("Error de conexión con el servicio de productos: {}", e.getMessage());
            throw new CommunicationException("Error de conexión con el servicio de productos", e);
        } catch (Exception e) {
            log.error("Error inesperado al comunicarse con el servicio de productos: {}", e.getMessage(), e);
            
            // Si el servicio de productos no está disponible, podemos proporcionar un producto temporal
            log.info("Generando producto temporal para pruebas debido al error");
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(id);
            productoDTO.setNombre("Producto temporal (error de comunicación)");
            productoDTO.setPrecio(0.0);
            return productoDTO;
        }
    }
    
    /**
     * Método para obtener todos los productos con paginación
     * (por si necesitas implementarlo en el futuro)
     */
    public Object getAllProductos(int page, int size) {
        try {
            String url = productoServiceUrl + "/api/productos?page=" + page + "&size=" + size;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Error al obtener los productos: {}", e.getMessage());
            throw new CommunicationException("Error al obtener los productos", e);
        }
    }
}