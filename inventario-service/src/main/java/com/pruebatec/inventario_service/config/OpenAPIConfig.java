package com.pruebatec.inventario_service.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Servidor de desarrollo");

        Contact contact = new Contact();
        contact.setName("Soporte Técnico");
        contact.setEmail("soporte@pruebatec.com");
        contact.setUrl("https://www.pruebatec.com");

        License mitLicense = new License()
                .name("Licencia MIT")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("API de Gestión de Inventario")
                .version("1.0")
                .contact(contact)
                .description("Esta API proporciona endpoints para gestionar el inventario de productos.")
                .termsOfService("https://www.pruebatec.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}