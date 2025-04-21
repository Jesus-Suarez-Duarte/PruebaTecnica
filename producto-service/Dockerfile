# Usamos una imagen base de Java 21
FROM eclipse-temurin:21-jdk-alpine

# Etiquetas informativas
LABEL maintainer="Prueba Técnica <prueba@example.com>"
LABEL version="1.0"
LABEL description="Microservicio de Productos"

# Directorio de trabajo
WORKDIR /app

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Copiar el JAR y wallet de Oracle (si se usa)
COPY target/producto-service-0.0.1-SNAPSHOT.jar /app/app.jar
COPY src/main/resources/wallet /app/src/main/resources/wallet

# Puerto expuesto
EXPOSE 8080

# Punto de entrada para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]