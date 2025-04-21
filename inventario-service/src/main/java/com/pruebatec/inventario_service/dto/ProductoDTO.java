package com.pruebatec.inventario_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    private Long id;
    
    private String nombre;
    
    private Double precio;
}