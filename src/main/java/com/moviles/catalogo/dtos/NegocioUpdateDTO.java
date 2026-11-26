package com.moviles.catalogo.dtos;

public record NegocioUpdateDTO(
    String nombreNegocio,
    String direccionNegocio,
    String telefonoNegocio,
    String fotoNegocio,
    String passwordNegocio
) {}
