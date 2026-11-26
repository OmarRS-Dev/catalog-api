package com.moviles.catalogo.dtos;

import jakarta.validation.constraints.Size;

public record NegocioUpdateDTO(
    @Size(max = 100) String nombreNegocio,
    String direccionNegocio,
    String telefonoNegocio,
    String fotoNegocio,
    @Size(min = 8, max = 72) String passwordNegocio
) {}
