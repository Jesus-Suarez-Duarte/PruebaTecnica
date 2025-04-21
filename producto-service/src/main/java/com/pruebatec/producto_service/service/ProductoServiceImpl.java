package com.pruebatec.producto_service.service;

import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.entity.Producto;
import com.pruebatec.producto_service.exception.ProductoConDependenciasException;
import com.pruebatec.producto_service.exception.ResourceNotFoundException;
import com.pruebatec.producto_service.mapper.ProductoMapper;
import com.pruebatec.producto_service.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    
    @Override
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        log.debug("Creando nuevo producto: {}", productoDTO);
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDTO(producto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorId(Long id) {
        log.debug("Buscando producto con id: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return productoMapper.toDTO(producto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> listarProductos(Pageable pageable) {
        log.debug("Listando productos con paginación: {}", pageable);
        return productoRepository.findAll(pageable)
                .map(productoMapper::toDTO);
    }
    
    @Override
    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        log.debug("Actualizando producto con id: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        producto.setNombre(productoDTO.getNombre());
        
        // Convertir Double a BigDecimal correctamente
        if (productoDTO.getPrecio() != null) {
            producto.setPrecio(BigDecimal.valueOf(productoDTO.getPrecio()));
        }
        
        producto = productoRepository.save(producto);
        return productoMapper.toDTO(producto);
    }
    
    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        log.debug("Eliminando producto con id: {}", id);
        
        // Verificar si existe
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        try {
            // Intentar eliminar el producto
            productoRepository.delete(producto);
            // Forzar el flush para que la operación se ejecute inmediatamente
            productoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Esta excepción se lanza cuando hay restricciones de integridad referencial
            String mensaje = "No se puede eliminar el producto con ID " + id + 
                           " porque tiene registros asociados en inventario. " +
                           "Elimine primero los registros de inventario.";
            
            log.error(mensaje);
            throw new ProductoConDependenciasException(id, mensaje);
        }
    }
}