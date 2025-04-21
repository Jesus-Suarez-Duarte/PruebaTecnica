package com.pruebatec.inventario_service.repository;


import com.pruebatec.inventario_service.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    Optional<Inventario> findByProductoId(Long productoId);
}
