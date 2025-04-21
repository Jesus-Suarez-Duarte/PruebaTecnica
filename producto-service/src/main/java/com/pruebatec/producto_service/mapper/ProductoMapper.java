package com.pruebatec.producto_service.mapper;

import com.pruebatec.producto_service.dto.ProductoDTO;
import com.pruebatec.producto_service.entity.Producto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        
        // Convertir BigDecimal a Double
        if (producto.getPrecio() != null) {
            dto.setPrecio(producto.getPrecio().doubleValue());
        }
        
        return dto;
    }
    
    public List<ProductoDTO> toDTOList(List<Producto> productos) {
        if (productos == null) {
            return null;
        }
        
        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public Producto toEntity(ProductoDTO productoDTO) {
        if (productoDTO == null) {
            return null;
        }
        
        Producto producto = new Producto();
        producto.setId(productoDTO.getId());
        producto.setNombre(productoDTO.getNombre());
        
        // Convertir Double a BigDecimal
        if (productoDTO.getPrecio() != null) {
            producto.setPrecio(BigDecimal.valueOf(productoDTO.getPrecio()));
        }
        
        return producto;
    }
}