package com.lifementor.config;

import com.lifementor.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT Authentication Filter
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            HandlerExceptionResolver handlerExceptionResolver,
            com.lifementor.service.TokenService tokenService) {
        return new JwtAuthenticationFilter(tokenService, handlerExceptionResolver);
    }

    // Main security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                // Disable CSRF for REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless session (JWT ready)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - explicitly list each auth endpoint
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/validate-token",
                                "/error",
                                "/files/**"
                        ).permitAll()
                        // Protected endpoints - require authentication
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/profile/**").authenticated()
                        .requestMatchers("/api/lifestyle-assessment/**").authenticated()
                        .requestMatchers("/api/ai-feedback/**").authenticated()
                        .requestMatchers("/api/ai-chat/**").authenticated()           
                        .requestMatchers("/api/daily-checkin/**").authenticated()     
                        .requestMatchers("/api/goals/**").authenticated()             
                        .requestMatchers("/api/wellbeing/**").authenticated()         
                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )

                // Add JWT filter - this will only check authenticated endpoints
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Disable default login mechanisms
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // IMPORTANT: Use allowedOriginPatterns instead of allowedOrigins
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}