package com.moviles.catalogo.dtos;

public record NegocioRequestDTO(
    String nombreNegocio,
    String direccionNegocio,
    String telefonoNegocio,
    String fotoNegocio,
    String passwordNegocio
) {}
