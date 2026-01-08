package com.moviles.catalogo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviles.catalogo.entities.Producto;
import com.moviles.catalogo.services.ProductoService;

@SuppressWarnings("null")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Crear producto
    @PostMapping("/guardar")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevo = productoService.guardarProducto(producto);
        return ResponseEntity.ok(nuevo);
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Optional<Producto> encontrado = productoService.obtenerPorId(id);
        return encontrado.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto actualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    //Traer productos de negocio
    @GetMapping("/negocio/{idNegocio}")
    public ResponseEntity<List<Producto>> obtenerProductosPorNegocio(
            @PathVariable Long idNegocio) {

        List<Producto> productos = productoService.obtenerProductosPorNegocio(idNegocio);
        return ResponseEntity.ok(productos);
    }

    //buscar por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String query) {
        try {
            List<Producto> productos = productoService.buscarProductos(query);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

