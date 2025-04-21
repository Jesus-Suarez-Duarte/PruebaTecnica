package com.pruebatec.inventario_service.event;


import com.pruebatec.inventario_service.dto.InventarioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Publicador de eventos simples para el cambio de inventario
 * En un escenario real, aquí se implementaría la integración con 
 * un sistema de mensajería como Kafka, RabbitMQ, etc.
 */
@Component
public class InventarioEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InventarioEventPublisher.class);
    
    /**
     * Publica un evento de cambio de inventario
     * @param inventario Información del inventario modificado
     * @param tipoOperacion Tipo de operación (ACTUALIZACIÓN, COMPRA, REPOSICIÓN)
     */
    public void publicarCambioInventario(InventarioDTO inventario, TipoOperacion tipoOperacion) {
        log.info("EVENTO DE INVENTARIO: {} - Producto ID: {}, Nueva Cantidad: {}", 
                tipoOperacion.name(), 
                inventario.getProductoId(), 
                inventario.getCantidad());
        
        // Aquí se implementaría la lógica para enviar el evento a un sistema de mensajería
        // Por ejemplo:
        // kafkaTemplate.send("inventario-eventos", objectMapper.writeValueAsString(inventario));
    }
    
    public enum TipoOperacion {
        ACTUALIZACIÓN,
        COMPRA,
        REPOSICIÓN
    }
}