# PruebaTecnica
Desarrollo de prueba tecnica
# Sistema de Gestión de Productos e Inventario

Este proyecto implementa un sistema de microservicios para la gestión de productos e inventario, siguiendo los estándares JSON:API para la comunicación entre servicios.

## Descripción del Proyecto

El sistema está compuesto por dos microservicios independientes:

1. **Servicio de Productos (puerto 8080)**: Gestiona el catálogo de productos con operaciones CRUD.
2. **Servicio de Inventario (puerto 8081)**: Gestiona el inventario de productos, interactuando con el servicio de productos.

### Funcionalidades Principales

- Gestión completa de productos (crear, leer, actualizar, eliminar)
- Consulta de inventario con información detallada de productos
- Registro de compras y reposiciones de inventario
- Emisión de eventos cuando cambia el inventario
- Comunicación segura entre servicios mediante API keys

## Tecnologías Utilizadas

- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- Oracle Database / H2 (bases de datos)
- Swagger/OpenAPI (documentación)
- JUnit y Mockito (pruebas)
- JaCoCo (cobertura de código)
- Docker y Docker Compose

## Requisitos Previos

- JDK 21
- Maven
- Docker y Docker Compose (opcional, para despliegue con contenedores)
- Oracle Database (para entornos de desarrollo y producción)

## Estructura del Proyecto

```
.
├── docker-compose.yml
├── producto-service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       └── test/
└── inventario-service
    ├── Dockerfile
    ├── pom.xml
    └── src/
        ├── main/
        └── test/
```

## Configuración y Ejecución

### Configuración

El proyecto soporta tres perfiles de Spring:

- **test**: Utiliza H2 (base de datos en memoria) para pruebas
- **dev**: Configura Oracle 21c XE local
- **prod**: Configura Oracle Cloud Autonomous Database

### Ejecución Local con Maven

1. Clonar el repositorio:
   ```
   git clone https://github.com/tu-usuario/tu-repositorio.git
   cd tu-repositorio
   ```

2. Compilar los proyectos:
   ```
   cd producto-service
   mvn clean package
   cd ../inventario-service
   mvn clean package
   ```

3. Ejecutar los servicios (en terminales separadas):
   ```
   # Servicio de Productos
   java -jar -Dspring.profiles.active=dev producto-service/target/producto-service-0.0.1-SNAPSHOT.jar
   
   # Servicio de Inventario
   java -jar -Dspring.profiles.active=dev inventario-service/target/inventario-service-0.0.1-SNAPSHOT.jar
   ```

### Ejecución con Docker

1. Construir y ejecutar los contenedores:
   ```
   docker-compose build
   docker-compose up -d
   ```

2. Verificar el estado de los servicios:
   ```
   docker-compose ps
   ```

3. Detener los servicios:
   ```
   docker-compose down
   ```

## Documentación de la API

Una vez que los servicios estén en ejecución, puedes acceder a la documentación Swagger/OpenAPI:

- Servicio de Productos: http://localhost:8080/swagger-ui.html
- Servicio de Inventario: http://localhost:8081/swagger-ui.html

## Pruebas

El proyecto incluye pruebas unitarias y de integración. Para ejecutarlas:

```
cd producto-service
mvn test

cd ../inventario-service
mvn test
```

Para generar informes de cobertura con JaCoCo:

```
mvn verify
```

Los informes se generarán en `target/site/jacoco/index.html`.

## Diagrama de Arquitectura

```
                 HTTP/JSON:API
┌──────────────┐ <-------------> ┌──────────────┐
│              │                 │              │
│   Producto   │ <-------------> │  Inventario  │
│   Service    │     API Keys    │   Service    │
│  (Puerto 8080)│                 │ (Puerto 8081)│
│              │                 │              │
└──────────────┘                 └──────────────┘
       │                                │
       │                                │
       ▼                                ▼
┌──────────────┐                 ┌──────────────┐
│              │                 │              │
│ Base de Datos│                 │ Base de Datos│
│  Productos   │                 │  Inventario  │
│              │                 │              │
└──────────────┘                 └──────────────┘
```

## Decisiones Técnicas

1. **Arquitectura de Microservicios**: Se eligió esta arquitectura para permitir el desarrollo, despliegue y escalamiento independiente de los servicios.

2. **JSON:API**: Se implementó este estándar para proporcionar respuestas consistentes y facilitar la integración.

3. **Base de Datos**: 
   - Oracle para entornos de producción y desarrollo debido a su robustez y soporte para transacciones complejas.
   - H2 para pruebas por su facilidad de configuración y uso en memoria.

4. **Manejo de Excepciones Centralizado**: Cada servicio implementa un manejador global de excepciones para respuestas de error consistentes.

5. **Patrón DTO**: Se utilizan DTOs para separar la representación externa de la representación interna de los datos.

## Contribución

1. Haz un fork del proyecto
2. Crea tu rama de características (`git checkout -b feature/nueva-caracteristica`)
3. Haz commit de tus cambios (`git commit -am 'Añadir nueva característica'`)
4. Haz push a la rama (`git push origin feature/nueva-caracteristica`)
5. Abre un Pull Request



## Contacto

Para preguntas o sugerencias, por favor contacta a [Jesus Antonio Suarez Duarte/Jebus702@hotmail.com].
