package com.pruebatec.producto_service.config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OpenAPIConfigTest {

    @InjectMocks
    private OpenAPIConfig openAPIConfig;

    private final String serverPort = "8080";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(openAPIConfig, "serverPort", serverPort);
    }

    @Test
    public void testMyOpenAPI() {
        // Act
        OpenAPI openAPI = openAPIConfig.myOpenAPI();

        // Assert
        assertNotNull(openAPI);
        
        // Verify info
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("API de Gestión de Productos", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertTrue(info.getDescription().contains("endpoints para gestionar productos"));
        assertEquals("https://www.pruebatec.com/terms", info.getTermsOfService());
        
        // Verify contact
        Contact contact = info.getContact();
        assertNotNull(contact);
        assertEquals("Soporte Técnico", contact.getName());
        assertEquals("soporte@pruebatec.com", contact.getEmail());
        assertEquals("https://www.pruebatec.com", contact.getUrl());
        
        // Verify license
        License license = info.getLicense();
        assertNotNull(license);
        assertEquals("Licencia MIT", license.getName());
        assertEquals("https://choosealicense.com/licenses/mit/", license.getUrl());
        
        // Verify servers
        assertNotNull(openAPI.getServers());
        assertEquals(1, openAPI.getServers().size());
        
        Server server = openAPI.getServers().get(0);
        assertNotNull(server);
        assertEquals("http://localhost:" + serverPort, server.getUrl());
        assertEquals("Servidor de desarrollo", server.getDescription());
    }
}