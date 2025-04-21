package com.pruebatec.inventario_service.service.impl;

import com.pruebatec.inventario_service.dto.InventarioDTO;
import com.pruebatec.inventario_service.dto.ProductoDTO;
import com.pruebatec.inventario_service.entity.Inventario;
import com.pruebatec.inventario_service.event.InventarioEventPublisher;
import com.pruebatec.inventario_service.exception.CommunicationException;
import com.pruebatec.inventario_service.exception.ResourceNotFoundException;
import com.pruebatec.inventario_service.mapper.InventarioMapper;
import com.pruebatec.inventario_service.repository.InventarioRepository;
import com.pruebatec.inventario_service.service.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioServiceImpl implements InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioServiceImpl.class);
    
    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;
    private final ProductoClientServiceImpl productoClientService;
    private final InventarioEventPublisher eventPublisher;
    
    public InventarioServiceImpl(
            InventarioRepository inventarioRepository,
            InventarioMapper inventarioMapper,
            ProductoClientServiceImpl productoClientService,
            InventarioEventPublisher eventPublisher) {
        this.inventarioRepository = inventarioRepository;
        this.inventarioMapper = inventarioMapper;
        this.productoClientService = productoClientService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public InventarioDTO getInventarioByProductoId(Long productoId) {
        log.info("Obteniendo inventario para el producto ID: {}", productoId);
        
        // Buscar inventario por ID de producto
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    // Si no existe, crear uno nuevo con cantidad 0
                    log.info("No se encontró inventario para el producto ID: {}. Creando nuevo.", productoId);
                    Inventario nuevo = new Inventario();
                    nuevo.setProductoId(productoId);
                    nuevo.setCantidad(0);
                    return inventarioRepository.save(nuevo);
                });
        
        // Convertir a DTO
        InventarioDTO inventarioDTO = inventarioMapper.toDTO(inventario);
        
        try {
            // Obtener información del producto desde el servicio de productos
            ProductoDTO productoDTO = productoClientService.getProductoById(productoId);
            inventarioDTO.setProducto(productoDTO);
        } catch (CommunicationException e) {
            log.error("Error al comunicarse con el servicio de productos: {}", e.getMessage());
            // Crear un producto básico para pruebas
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(productoId);
            productoDTO.setNombre("Producto temporal (error de comunicación)");
            productoDTO.setPrecio(0.0);
            inventarioDTO.setProducto(productoDTO);
            
            // Re-lanzar la excepción para mantener el comportamiento esperado
            throw e;
        }
        
        return inventarioDTO;
    }

    @Override
    @Transactional
    public InventarioDTO actualizarCantidad(Long productoId, Integer cantidad) {
        log.info("Actualizando cantidad en inventario para producto ID: {} a {}", productoId, cantidad);
        
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        
        // Buscar inventario por ID de producto
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    try {
                        // Si no existe, verificar que el producto existe en el servicio de productos
                        productoClientService.getProductoById(productoId);
                    } catch (CommunicationException e) {
                        log.warn("No se pudo verificar el producto. Asumiendo que existe.");
                    }
                    
                    // Crear nuevo registro de inventario
                    Inventario nuevo = new Inventario();
                    nuevo.setProductoId(productoId);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
        
        // Actualizar cantidad
        inventario.setCantidad(cantidad);
        
        // Guardar en base de datos
        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        
        // Convertir a DTO
        InventarioDTO inventarioDTO = inventarioMapper.toDTO(inventarioActualizado);
        
        // Publicar evento de cambio de inventario
        eventPublisher.publicarCambioInventario(inventarioDTO, InventarioEventPublisher.TipoOperacion.ACTUALIZACIÓN);
        
        return inventarioDTO;
    }
    
    @Override
    @Transactional
    public InventarioDTO registrarCompra(Long productoId, Integer cantidadComprada) {
        log.info("Registrando compra para producto ID: {}, cantidad: {}", productoId, cantidadComprada);
        
        if (cantidadComprada <= 0) {
            throw new IllegalArgumentException("La cantidad comprada debe ser mayor a cero");
        }
        
        // Buscar inventario por ID de producto
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    try {
                        // Si no existe, verificar que el producto existe en el servicio de productos
                        productoClientService.getProductoById(productoId);
                    } catch (CommunicationException e) {
                        log.warn("No se pudo verificar el producto. Asumiendo que existe.");
                    }
                    
                    // Crear nuevo registro de inventario
                    Inventario nuevo = new Inventario();
                    nuevo.setProductoId(productoId);
                    nuevo.setCantidad(0);
                    return inventarioRepository.save(nuevo);
                });
        
        // Verificar si hay suficiente stock
        if (inventario.getCantidad() < cantidadComprada) {
            throw new IllegalArgumentException(
                    String.format("Stock insuficiente. Stock actual: %d, Cantidad solicitada: %d", 
                            inventario.getCantidad(), cantidadComprada));
        }
        
        // Actualizar cantidad (restar la cantidad comprada)
        inventario.setCantidad(inventario.getCantidad() - cantidadComprada);
        
        // Guardar en base de datos
        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        
        // Convertir a DTO
        InventarioDTO inventarioDTO = inventarioMapper.toDTO(inventarioActualizado);
        
        // Publicar evento de cambio de inventario
        eventPublisher.publicarCambioInventario(inventarioDTO, InventarioEventPublisher.TipoOperacion.COMPRA);
        
        return inventarioDTO;
    }
    
    @Override
    @Transactional
    public InventarioDTO registrarReposicion(Long productoId, Integer cantidadRepuesta) {
        log.info("Registrando reposición de stock para producto ID: {}, cantidad: {}", productoId, cantidadRepuesta);
        
        if (cantidadRepuesta <= 0) {
            throw new IllegalArgumentException("La cantidad repuesta debe ser mayor a cero");
        }
        
        // Buscar inventario por ID de producto
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    try {
                        // Si no existe, verificar que el producto existe en el servicio de productos
                        productoClientService.getProductoById(productoId);
                    } catch (CommunicationException e) {
                        log.warn("No se pudo verificar el producto. Asumiendo que existe.");
                    }
                    
                    // Crear nuevo registro de inventario
                    Inventario nuevo = new Inventario();
                    nuevo.setProductoId(productoId);
                    nuevo.setCantidad(0);
                    return inventarioRepository.save(nuevo);
                });
        
        // Actualizar cantidad (sumar la cantidad repuesta)
        inventario.setCantidad(inventario.getCantidad() + cantidadRepuesta);
        
        // Guardar en base de datos
        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        
        // Convertir a DTO
        InventarioDTO inventarioDTO = inventarioMapper.toDTO(inventarioActualizado);
        
        // Publicar evento de cambio de inventario
        eventPublisher.publicarCambioInventario(inventarioDTO, InventarioEventPublisher.TipoOperacion.REPOSICIÓN);
        
        return inventarioDTO;
    }
}