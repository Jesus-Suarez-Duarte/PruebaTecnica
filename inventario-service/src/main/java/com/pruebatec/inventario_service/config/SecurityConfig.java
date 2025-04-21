package com.pruebatec.inventario_service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Value("${api.key}")
    private String apiKey;

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiKeyFilter(apiKey));
        registrationBean.addUrlPatterns("/api/*"); // Aplica el filtro a todas las rutas API
        return registrationBean;
    }

    public static class ApiKeyFilter implements Filter {

        private final String apiKey;

        public ApiKeyFilter(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            String path = httpRequest.getRequestURI();
            
            // Excluir rutas de Swagger y actuator del filtro
            if (path.contains("/swagger-ui") || path.contains("/api-docs") || path.contains("/actuator")) {
                chain.doFilter(request, response);
                return;
            }
            
            String requestApiKey = httpRequest.getHeader("X-API-KEY");
            
            if (apiKey.equals(requestApiKey)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"errors\":[{\"status\":\"401\",\"title\":\"No autorizado\",\"detail\":\"API Key inválida\"}]}");
            }
        }

    }
}