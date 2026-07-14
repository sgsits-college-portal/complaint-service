package com.collegeportal.complaint_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;  

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    // This pulls the secret key from your application.properties
    @Value("${college.app.jwtSecret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Extract the Authorization header
        String header = request.getHeader("Authorization");

        // 2. If there is no token, let the request pass through (SecurityConfig will block it later if required)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Strip the "Bearer " prefix to get the raw token
        String token = header.substring(7);

        try {
            // 4. Decode and verify the token using your exact secret key
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 5. Extract the user's identity and custom claims
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            String subRole = claims.get("subRole", String.class);

            // 6. Map the extracted claims to Spring Security Authorities
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            if (subRole != null && !subRole.equals("NONE")) {
                authorities.add(new SimpleGrantedAuthority("SUB_" + subRole));
            }

            // 7. Tell Spring Security exactly who this user is and what they are allowed to do
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // If the token is expired, tampered with, or signed with the wrong key, wipe the context.
            SecurityContextHolder.clearContext();
        }

        // 8. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}