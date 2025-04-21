package com.pruebatec.inventario_service.mapper;

import com.pruebatec.inventario_service.dto.InventarioDTO;
import com.pruebatec.inventario_service.entity.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    public InventarioDTO toDTO(Inventario inventario) {
        if (inventario == null) {
            return null;
        }
        
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(inventario.getProductoId());
        dto.setCantidad(inventario.getCantidad());
        return dto;
    }
    
    public Inventario toEntity(InventarioDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Inventario entity = new Inventario();
        entity.setProductoId(dto.getProductoId());
        entity.setCantidad(dto.getCantidad());
        return entity;
    }
}