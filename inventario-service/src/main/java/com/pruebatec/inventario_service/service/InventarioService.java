package com.pruebatec.inventario_service.service;


import com.pruebatec.inventario_service.dto.InventarioDTO;

public interface InventarioService {
    
    /**
     * Obtiene la información de inventario por ID de producto,
     * incluyendo la información del producto obtenida del servicio de productos
     * @param productoId ID del producto
     * @return InventarioDTO con información completa
     */
    InventarioDTO getInventarioByProductoId(Long productoId);
    
    /**
     * Actualiza la cantidad de un producto en el inventario
     * @param productoId ID del producto
     * @param cantidad Nueva cantidad total
     * @return InventarioDTO actualizado
     */
    InventarioDTO actualizarCantidad(Long productoId, Integer cantidad);
    
    /**
     * Registra una compra de producto, disminuyendo la cantidad disponible
     * @param productoId ID del producto
     * @param cantidadComprada Cantidad comprada que se descontará del inventario
     * @return InventarioDTO actualizado
     */
    InventarioDTO registrarCompra(Long productoId, Integer cantidadComprada);
    
    /**
     * Registra una reposición de stock, aumentando la cantidad disponible
     * @param productoId ID del producto
     * @param cantidadRepuesta Cantidad añadida al inventario
     * @return InventarioDTO actualizado
     */
    InventarioDTO registrarReposicion(Long productoId, Integer cantidadRepuesta);
}