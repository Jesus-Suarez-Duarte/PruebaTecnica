package com.pruebatec.inventario_service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    
    @JsonProperty("producto_id")
    private Long productoId;
    
    private Integer cantidad;
    
    private ProductoDTO producto;
}
