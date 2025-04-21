package com.pruebatec.producto_service.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private String apiKey = "test-api-key";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(securityConfig, "apiKey", apiKey);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testApiKeyFilterRegistration() {
        // Act
        FilterRegistrationBean<SecurityConfig.ApiKeyFilter> filterRegistrationBean = securityConfig.apiKeyFilter();

        // Assert
        assertNotNull(filterRegistrationBean);
        assertTrue(filterRegistrationBean.getFilter() instanceof SecurityConfig.ApiKeyFilter);
        assertEquals("/api/*", filterRegistrationBean.getUrlPatterns().iterator().next());
        assertTrue(filterRegistrationBean.getOrder() <= 0); // Highest precedence
    }

    @Test
    public void testApiKeyFilter_ValidKey() throws ServletException, IOException {
        // Arrange
        SecurityConfig.ApiKeyFilter apiKeyFilter = new SecurityConfig.ApiKeyFilter(apiKey);
        request.addHeader("X-API-KEY", apiKey);

        // Act
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus()); // Default status
    }

    @Test
    public void testApiKeyFilter_InvalidKey() throws ServletException, IOException {
        // Arrange
        SecurityConfig.ApiKeyFilter apiKeyFilter = new SecurityConfig.ApiKeyFilter(apiKey);
        request.addHeader("X-API-KEY", "wrong-key");

        // Act
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertTrue(response.getContentAsString().contains("API Key inválida"));
    }

    @Test
    public void testApiKeyFilter_MissingKey() throws ServletException, IOException {
        // Arrange
        SecurityConfig.ApiKeyFilter apiKeyFilter = new SecurityConfig.ApiKeyFilter(apiKey);
        // No header added to request

        // Act
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertTrue(response.getContentAsString().contains("API Key inválida"));
    }
}
