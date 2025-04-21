package com.pruebatec.inventario_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {

    @Id
    @Column(name = "producto_id")
    private Long productoId;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}