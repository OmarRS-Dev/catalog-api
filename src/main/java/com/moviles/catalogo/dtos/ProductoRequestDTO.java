package com.moviles.catalogo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoRequestDTO(
    @NotBlank String nombreProducto,
    String descripcionProducto,
    @NotNull Long idCategoria,
    String portadaProducto,
    @PositiveOrZero double precioProducto
) {}
