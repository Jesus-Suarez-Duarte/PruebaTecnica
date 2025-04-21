-- Eliminar tablas si existen
DROP TABLE IF EXISTS inventarios;
DROP TABLE IF EXISTS productos;

-- Crear tabla productos
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);

-- Crear tabla inventarios con relación a productos
CREATE TABLE inventarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);