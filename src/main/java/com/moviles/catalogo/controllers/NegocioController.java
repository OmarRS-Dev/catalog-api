package com.moviles.catalogo.controllers;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moviles.catalogo.dtos.NegocioLoginDTO;
import com.moviles.catalogo.dtos.NegocioRequestDTO;
import com.moviles.catalogo.dtos.NegocioResponseDTO;
import com.moviles.catalogo.dtos.NegocioUpdateDTO;
import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.services.NegocioService;

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
    public ResponseEntity<NegocioResponseDTO> crearNegocio(@RequestBody NegocioRequestDTO negocio) {
        Negocio nuevo = negocioService.guardarNegocio(negocio);
        return ResponseEntity.ok(aNegocioResponse(nuevo));
    }

    // -------------------------------
    // 2️⃣ Obtener un negocio por ID (GET)
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<NegocioResponseDTO> obtenerPorId(@PathVariable Long id) {
        Optional<Negocio> encontrado = negocioService.obtenerPorId(id);
        return encontrado.map(n -> ResponseEntity.ok(aNegocioResponse(n)))
                         .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------
    // 3️⃣ Actualizar un negocio (PUT)
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<NegocioResponseDTO> actualizarNegocio(@PathVariable Long id, @RequestBody NegocioUpdateDTO negocio) {
        try {
            Negocio actualizado = negocioService.actualizarNegocio(id, negocio);
            return ResponseEntity.ok(aNegocioResponse(actualizado));
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
    public ResponseEntity<?> login(@RequestBody NegocioLoginDTO loginRequest) {
        Optional<Negocio> negocio = negocioService.login(
            loginRequest.nombreNegocio(),
            loginRequest.passwordNegocio()
        );

        if (negocio.isPresent()) {
            return ResponseEntity.ok(aNegocioResponse(negocio.get()));
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    private NegocioResponseDTO aNegocioResponse(Negocio negocio) {
        return new NegocioResponseDTO(
            negocio.getIdNegocio(),
            negocio.getNombreNegocio(),
            negocio.getDireccionNegocio(),
            negocio.getTelefonoNegocio(),
            negocio.getFotoNegocio()
        );
    }
}
