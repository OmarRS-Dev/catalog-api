package com.moviles.catalogo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NegocioRequestDTO(
    @NotBlank @Size(max = 100) String nombreNegocio,
    String direccionNegocio,
    String telefonoNegocio,
    String fotoNegocio,
    @NotBlank @Size(min = 8, max = 72) String passwordNegocio
) {}
