package com.pruebatec.producto_service.service;


import com.pruebatec.producto_service.dto.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    
    ProductoDTO crearProducto(ProductoDTO productoDTO);
    
    ProductoDTO obtenerProductoPorId(Long id);
    
    Page<ProductoDTO> listarProductos(Pageable pageable);
    
    ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO);
    
    void eliminarProducto(Long id);
}