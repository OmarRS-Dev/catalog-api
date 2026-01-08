package com.moviles.catalogo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moviles.catalogo.entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findAllByOrderByIdProductoDesc();
    List<Producto> findByNegocioIdNegocioOrderByIdProductoDesc(Long idNegocio);
    List<Producto> findByNombreProductoContainingIgnoreCaseOrDescripcionProductoContainingIgnoreCase(
        String nombreProducto, String descripcionProducto
    );

}
