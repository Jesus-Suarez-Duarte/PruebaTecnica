package com.pruebatec.producto_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un producto")
public class ProductoDTO {
    
    @Schema(description = "Identificador único del producto", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del producto", example = "Smartphone Galaxy S21")
    private String nombre;
    
    @Schema(description = "Precio del producto", example = "799.99")
    private Double precio;
}