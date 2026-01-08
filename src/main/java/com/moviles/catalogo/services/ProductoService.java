package com.moviles.catalogo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import com.moviles.catalogo.entities.Producto;

public interface ProductoService {
    Producto guardarProducto(@NonNull Producto producto);
    Optional<Producto> obtenerPorId(@NonNull Long id);
    List<Producto> listarTodos();
    Producto actualizarProducto(@NonNull Long id, Producto producto);
    void eliminarProducto(@NonNull Long id);
    public List<Producto> obtenerProductosPorNegocio(@NonNull Long idNegocio);
    public List<Producto> buscarProductos(String query);
}
