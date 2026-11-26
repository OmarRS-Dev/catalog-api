package com.moviles.catalogo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviles.catalogo.dtos.CategoriaResponseDTO;
import com.moviles.catalogo.repositories.CategoriaRepository;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaRepository.findAll()
            .stream()
            .map(categoria -> new CategoriaResponseDTO(
                categoria.getIdCategoria(),
                categoria.getNombreCategoria()
            ))
            .toList();
        return ResponseEntity.ok(categorias);
    }
}
