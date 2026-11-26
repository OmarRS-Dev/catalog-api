package com.moviles.catalogo.dtos;

public record ProductoResponseDTO(
    Long idProducto,
    String nombreProducto,
    String descripcionProducto,
    CategoriaResponseDTO categoria,
    NegocioResponseDTO negocio,
    String portadaProducto,
    double precioProducto
) {}
