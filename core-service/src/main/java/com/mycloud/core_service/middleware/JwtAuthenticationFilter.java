package com.mycloud.core_service.middleware;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final String[] allowedEndpoints;

    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.allowedEndpoints = jwtConfig.getAllowedEndpoints();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        String authHeader = request.getHeader("Authorization");
        boolean tokenPresent = authHeader != null && authHeader.startsWith("Bearer ");

        for (String endpoint : allowedEndpoints) {
            if (requestPath.startsWith(endpoint)) {
                return !tokenPresent; // Run filter if token is present on public endpoints
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.ValidateToken(token)) {
                sendUnauthorized(response);
                return;
            }

            JwtUser user = new JwtUser(
                    jwtUtil.ExtractUserId(token),
                    jwtUtil.ExtractEmail(token)
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            sendUnauthorized(response);
        }
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Session timed out. Please login again.\"}");
    }
}