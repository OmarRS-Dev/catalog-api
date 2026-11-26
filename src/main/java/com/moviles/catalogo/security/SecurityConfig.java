package com.moviles.catalogo.security;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/negocios/guardar", "/api/negocios/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/negocios/**", "/api/productos/**", "/api/categorias/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Se requiere autenticacion")
                )
                .accessDeniedHandler((request, response, exception) ->
                    writeJson(response, HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para esta accion")
                )
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    private void writeJson(
        HttpServletResponse response,
        int status,
        String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            "{\"status\":" + status + ",\"error\":\"" + message + "\"}"
        );
    }
}
