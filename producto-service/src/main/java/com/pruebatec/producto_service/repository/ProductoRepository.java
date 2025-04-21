package com.pruebatec.producto_service.repository;



import com.pruebatec.producto_service.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí puedes añadir métodos personalizados de consulta si los necesitas
}