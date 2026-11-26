package com.moviles.catalogo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.moviles.catalogo.dtos.ProductoRequestDTO;
import com.moviles.catalogo.dtos.ProductoUpdateDTO;
import com.moviles.catalogo.entities.Categoria;
import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.entities.Producto;
import com.moviles.catalogo.repositories.CategoriaRepository;
import com.moviles.catalogo.repositories.NegocioRepository;
import com.moviles.catalogo.repositories.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final NegocioRepository negocioRepository;

    public ProductoServiceImpl(
        ProductoRepository productoRepository,
        CategoriaRepository categoriaRepository,
        NegocioRepository negocioRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.negocioRepository = negocioRepository;
    }

    @Override
    public Producto guardarProducto(@NonNull ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombreProducto(dto.nombreProducto());
        producto.setDescripcionProducto(dto.descripcionProducto());
        producto.setPortadaProducto(dto.portadaProducto());
        producto.setPrecioProducto(dto.precioProducto());
        producto.setCategoria(obtenerCategoria(dto.idCategoria()));
        producto.setNegocio(obtenerNegocio(dto.idNegocio()));
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
    public Producto actualizarProducto(@NonNull Long id, ProductoUpdateDTO dto) {
        return productoRepository.findById(id)
                .map(p -> {
                    p.setNombreProducto(dto.nombreProducto());
                    p.setDescripcionProducto(dto.descripcionProducto());
                    p.setCategoria(obtenerCategoria(dto.idCategoria()));
                    p.setNegocio(obtenerNegocio(dto.idNegocio()));
                    p.setPortadaProducto(dto.portadaProducto());
                    p.setPrecioProducto(dto.precioProducto());
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

    private Categoria obtenerCategoria(Long idCategoria) {
        return categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    private Negocio obtenerNegocio(Long idNegocio) {
        return negocioRepository.findById(idNegocio)
            .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
    }
}
