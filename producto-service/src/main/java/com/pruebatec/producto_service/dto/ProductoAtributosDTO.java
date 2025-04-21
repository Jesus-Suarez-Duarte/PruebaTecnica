package com.pruebatec.producto_service.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Atributos de un producto")
public class ProductoAtributosDTO {
    
    @Schema(description = "Nombre del producto", example = "Smartphone Galaxy S21")
    private String nombre;
    
    @Schema(description = "Precio del producto", example = "799.99")
    private Double precio;
}