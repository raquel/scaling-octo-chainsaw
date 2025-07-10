
package com.example.usermanagement.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        // Skip authentication for H2 console, actuator endpoints, and external API endpoints
        if (requestURI.startsWith("/h2-console") || 
            requestURI.startsWith("/actuator") ||
            requestURI.startsWith("/external-api")) {
            return true;
        }

        // For demo purposes, we'll use a simple token-based authentication
        // In production, this would integrate with JWT or OAuth2
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            response.setContentType("application/json");
            return false;
        }

        String token = authHeader.substring(7);
        
        // Simple token validation (in production, validate JWT or check against database)
        if (!"valid-token-123".equals(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            response.setContentType("application/json");
            return false;
        }

        // Add user info to request attributes for use in controllers
        request.setAttribute("userId", "demo-user");
        return true;
    }
}
