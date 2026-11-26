package com.moviles.catalogo.dtos;

public record LoginResponseDTO(
    String token,
    String tokenType,
    long expiresInSeconds,
    NegocioResponseDTO negocio
) {}
