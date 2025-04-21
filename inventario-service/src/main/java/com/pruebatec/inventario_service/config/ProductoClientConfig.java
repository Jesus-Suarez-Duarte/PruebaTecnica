package com.pruebatec.inventario_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ProductoClientConfig {

    @Value("${api.key}")
    private String apiKey;
    
    @Value("${resttemplate.connect.timeout:5000}")
    private int connectTimeout;
    
    @Value("${resttemplate.read.timeout:5000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        // Configurar factory con timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        
        // Crear RestTemplate con la factory
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Configurar interceptor para añadir la API key
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().add("X-API-KEY", apiKey);
            return execution.execute(request, body);
        });
        
        restTemplate.setInterceptors(interceptors);
        
        return restTemplate;
    }
}