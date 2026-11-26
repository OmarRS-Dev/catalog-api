package com.moviles.catalogo.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.repositories.NegocioRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final NegocioRepository negocioRepository;

    public JwtAuthenticationFilter(
        JwtService jwtService,
        NegocioRepository negocioRepository
    ) {
        this.jwtService = jwtService;
        this.negocioRepository = negocioRepository;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parseToken(authorization.substring(7));
                long idNegocio = Long.parseLong(claims.getSubject());
                Negocio negocio = negocioRepository.findById(idNegocio).orElse(null);

                if (negocio != null) {
                    NegocioPrincipal principal = new NegocioPrincipal(
                        negocio.getIdNegocio(),
                        negocio.getNombreNegocio()
                    );
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            Collections.emptyList()
                        );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | NumberFormatException ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
