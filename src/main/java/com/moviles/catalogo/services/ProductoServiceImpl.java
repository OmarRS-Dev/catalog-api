package com.moviles.catalogo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.moviles.catalogo.entities.Producto;
import com.moviles.catalogo.repositories.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto guardarProducto(@NonNull Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Optional<Producto> obtenerPorId(@NonNull Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAllByOrderByIdProductoDesc();
    }

    @Override
    public Producto actualizarProducto(@NonNull Long id, Producto producto) {
        return productoRepository.findById(id)
                .map(p -> {
                    p.setNombreProducto(producto.getNombreProducto());                    
                    p.setDescripcionProducto(producto.getDescripcionProducto());                    
                    p.setCategoria(producto.getCategoria());
                    p.setNegocio(producto.getNegocio());                    
                    p.setPortadaProducto(producto.getPortadaProducto());  
                    p.setPrecioProducto(producto.getPrecioProducto());  
                    return productoRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public void eliminarProducto(@NonNull Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> obtenerProductosPorNegocio(@NonNull Long idNegocio) {
        return productoRepository.findByNegocioIdNegocioOrderByIdProductoDesc(idNegocio);
    }

    @Override
    public List<Producto> buscarProductos(String query) {
        return productoRepository.findByNombreProductoContainingIgnoreCaseOrDescripcionProductoContainingIgnoreCase(query, query);
    }
}
