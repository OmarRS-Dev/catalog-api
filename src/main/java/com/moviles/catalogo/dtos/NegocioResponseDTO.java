package com.moviles.catalogo.dtos;

public record NegocioResponseDTO(
    long idNegocio,
    String nombreNegocio,
    String direccionNegocio,
    String telefonoNegocio,
    String fotoNegocio
) {}
