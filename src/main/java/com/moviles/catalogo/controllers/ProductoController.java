package com.moviles.catalogo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import com.moviles.catalogo.dtos.CategoriaResponseDTO;
import com.moviles.catalogo.dtos.ImageUploadResponseDTO;
import com.moviles.catalogo.dtos.NegocioResponseDTO;
import com.moviles.catalogo.dtos.ProductoRequestDTO;
import com.moviles.catalogo.dtos.ProductoResponseDTO;
import com.moviles.catalogo.dtos.ProductoUpdateDTO;
import com.moviles.catalogo.entities.Categoria;
import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.entities.Producto;
import com.moviles.catalogo.services.ProductoService;
import com.moviles.catalogo.services.CloudinaryImageService;
import com.moviles.catalogo.security.NegocioPrincipal;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CloudinaryImageService cloudinaryImageService;

    public ProductoController(
        ProductoService productoService,
        CloudinaryImageService cloudinaryImageService
    ) {
        this.productoService = productoService;
        this.cloudinaryImageService = cloudinaryImageService;
    }

    @PostMapping("/imagenes")
    public ResponseEntity<ImageUploadResponseDTO> subirImagen(
        @RequestParam("image") MultipartFile image,
        Authentication authentication
    ) {
        NegocioPrincipal principal = (NegocioPrincipal) authentication.getPrincipal();
        String secureUrl = cloudinaryImageService.uploadProductImage(
            image,
            principal.idNegocio()
        );
        return ResponseEntity.ok(new ImageUploadResponseDTO(secureUrl));
    }

    // Crear producto
    @PostMapping("/guardar")
    public ResponseEntity<ProductoResponseDTO> crearProducto(
        @Valid @RequestBody ProductoRequestDTO producto,
        Authentication authentication
    ) {
        NegocioPrincipal principal = (NegocioPrincipal) authentication.getPrincipal();
        Producto nuevo = productoService.guardarProducto(producto, principal.idNegocio());
        return ResponseEntity.ok(aProductoResponse(nuevo));
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        Optional<Producto> encontrado = productoService.obtenerPorId(id);
        return encontrado.map(p -> ResponseEntity.ok(aProductoResponse(p)))
                         .orElse(ResponseEntity.notFound().build());
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
            productoService.listarTodos().stream().map(this::aProductoResponse).toList()
        );
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
        @PathVariable Long id,
        @Valid @RequestBody ProductoUpdateDTO producto,
        Authentication authentication
    ) {
        NegocioPrincipal principal = (NegocioPrincipal) authentication.getPrincipal();
        Producto actualizado = productoService.actualizarProducto(
            id,
            producto,
            principal.idNegocio()
        );
        return ResponseEntity.ok(aProductoResponse(actualizado));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
        @PathVariable Long id,
        Authentication authentication
    ) {
        NegocioPrincipal principal = (NegocioPrincipal) authentication.getPrincipal();
        productoService.eliminarProducto(id, principal.idNegocio());
        return ResponseEntity.noContent().build();
    }

    //Traer productos de negocio
    @GetMapping("/negocio/{idNegocio}")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorNegocio(
            @PathVariable Long idNegocio) {

        List<Producto> productos = productoService.obtenerProductosPorNegocio(idNegocio);
        return ResponseEntity.ok(productos.stream().map(this::aProductoResponse).toList());
    }

    //buscar por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductos(@RequestParam String query) {
        try {
            List<Producto> productos = productoService.buscarProductos(query);
            return ResponseEntity.ok(productos.stream().map(this::aProductoResponse).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ProductoResponseDTO aProductoResponse(Producto producto) {
        Categoria categoria = producto.getCategoria();
        Negocio negocio = producto.getNegocio();

        CategoriaResponseDTO categoriaDTO = categoria == null
            ? null
            : new CategoriaResponseDTO(categoria.getIdCategoria(), categoria.getNombreCategoria());

        NegocioResponseDTO negocioDTO = negocio == null
            ? null
            : new NegocioResponseDTO(
                negocio.getIdNegocio(),
                negocio.getNombreNegocio(),
                negocio.getDireccionNegocio(),
                negocio.getTelefonoNegocio(),
                negocio.getFotoNegocio()
            );

        return new ProductoResponseDTO(
            producto.getIdProducto(),
            producto.getNombreProducto(),
            producto.getDescripcionProducto(),
            categoriaDTO,
            negocioDTO,
            producto.getPortadaProducto(),
            producto.getPrecioProducto()
        );
    }
}
