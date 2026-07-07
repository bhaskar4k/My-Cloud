package com.mycloud.common_service.config;

import com.mycloud.common_models.common_entities.JwtUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class DownstreamHeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String xUserId = request.getHeader("X-User-Id");
        String xUserEmail = request.getHeader("X-User-Email");
        String xIsAuthenticated = request.getHeader("X-User-Authenticated");

        JwtUser user;

        if ("true".equalsIgnoreCase(xIsAuthenticated) && xUserId != null && !xUserId.isBlank()) {
            try {
                // Fully Authenticated User
                user = new JwtUser(true, Long.parseLong(xUserId), xUserEmail);
            } catch (NumberFormatException e) {
                // Fallback to anonymous if header is corrupted
                user = new JwtUser(false, null, null);
            }
        } else {
            // Explicit Anonymous/Guest Visitor Context
            user = new JwtUser(false, null, null);
        }

        // Set the context regardless so GetCurrentUser() is never null, just cleanly unauthenticated
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}