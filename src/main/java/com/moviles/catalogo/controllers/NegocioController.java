package com.moviles.catalogo.controllers;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.services.NegocioService;

@SuppressWarnings("null")
@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    private final NegocioService negocioService;

    public NegocioController(NegocioService negocioService){
        this.negocioService = negocioService;
    }

    // -------------------------------
    // 1️⃣ Crear un negocio (POST)
    // -------------------------------
    @PostMapping("/guardar")
    public ResponseEntity<Negocio> crearNegocio(@RequestBody Negocio negocio) {
        Negocio nuevo = negocioService.guardarNegocio(negocio);
        return ResponseEntity.ok(nuevo);
    }

    // -------------------------------
    // 2️⃣ Obtener un negocio por ID (GET)
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Negocio> obtenerPorId(@PathVariable Long id) {
        Optional<Negocio> encontrado = negocioService.obtenerPorId(id);
        return encontrado.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------
    // 3️⃣ Actualizar un negocio (PUT)
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Negocio> actualizarNegocio(@PathVariable Long id, @RequestBody Negocio negocio) {
        try {
            Negocio actualizado = negocioService.actualizarNegocio(id, negocio);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------
    // 4️⃣ Eliminar un negocio (DELETE)
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNegocio(@PathVariable Long id) {
        negocioService.eliminarNegocio(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // Verificar el login
    // -------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Negocio loginRequest) {
        Optional<Negocio> negocio = negocioService.login(
            loginRequest.getNombreNegocio(),
            loginRequest.getPasswordNegocio()
        );

        if (negocio.isPresent()) {
            return ResponseEntity.ok(negocio.get());
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
}
