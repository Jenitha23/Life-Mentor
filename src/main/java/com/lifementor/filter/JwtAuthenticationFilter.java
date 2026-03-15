package com.lifementor.filter;

import com.lifementor.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final TokenService tokenService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    // List of public endpoints that don't need token validation
    private final List<String> publicEndpoints = Arrays.asList(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/auth/validate-token",
        "/error",
        "/files"
    );

    public JwtAuthenticationFilter(TokenService tokenService,
                                   HandlerExceptionResolver handlerExceptionResolver) {
        this.tokenService = tokenService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip filter for public endpoints
        return publicEndpoints.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (tokenService.validateToken(token)) {
                    // Extract user information from token
                    UUID userId = tokenService.extractUserId(token);
                    String email = tokenService.extractEmail(token);

                    // Set authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Set user ID as request attribute for controllers to use
                    request.setAttribute("userId", userId);
                    request.setAttribute("userEmail", email);

                    log.debug("User authenticated: {}", email);
                } else {
                    log.debug("Invalid token for request: {}", request.getRequestURI());
                }
            } else {
                log.debug("No Bearer token found in request: {}", request.getRequestURI());
            }

            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("JWT authentication failed for request {}: {}", 
                     request.getRequestURI(), e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}