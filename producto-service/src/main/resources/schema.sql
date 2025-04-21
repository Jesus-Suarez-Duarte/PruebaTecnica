-- Eliminar tablas si existen
DROP TABLE IF EXISTS productos;

-- Crear tabla productos
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);