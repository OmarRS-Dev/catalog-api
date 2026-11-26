package com.moviles.catalogo.dtos;

import jakarta.validation.constraints.NotBlank;

public record NegocioLoginDTO(
    @NotBlank String nombreNegocio,
    @NotBlank String passwordNegocio
) {}
