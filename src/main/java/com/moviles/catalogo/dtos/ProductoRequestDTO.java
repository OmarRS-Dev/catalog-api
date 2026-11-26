package com.moviles.catalogo.dtos;

public record ProductoRequestDTO(
    String nombreProducto,
    String descripcionProducto,
    Long idCategoria,
    Long idNegocio,
    String portadaProducto,
    double precioProducto
) {}
