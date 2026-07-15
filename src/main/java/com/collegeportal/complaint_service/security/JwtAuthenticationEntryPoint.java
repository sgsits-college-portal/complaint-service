package com.collegeportal.complaint_service.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // This ensures bad tokens return a 401 Unauthorized instead of a 403 Forbidden
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized - Invalid or missing JWT Token");
    }
}
