package com.amazio.api_gateway.security.config;

import com.amazio.api_gateway.security.filter.AuthFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/actuator/health").permitAll()

                        // Public product browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/products/**"
                        ).permitAll()

                        // Admin product operations
                        .requestMatchers("/api/v1/admin/products/**")
                        .hasRole("ADMIN")

                        // Customer and Admin user operations
                        .requestMatchers("/api/v1/users/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        // Customer address operations
                        .requestMatchers("/api/v1/addresses/**")
                        .hasRole("CUSTOMER")

                        // Order operations
                        .requestMatchers("/api/v1/orders/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        // Inventory operations
                        .requestMatchers("/api/v1/inventory/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"message\":\"Authentication required\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"message\":\"Access denied: insufficient permissions\"}"
                            );
                        })
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilterBefore(
                        authFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://amazio-frontend.vercel.app"
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}