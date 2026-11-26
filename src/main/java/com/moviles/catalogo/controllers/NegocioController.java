package com.moviles.catalogo.controllers;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.moviles.catalogo.dtos.LoginResponseDTO;
import com.moviles.catalogo.dtos.NegocioLoginDTO;
import com.moviles.catalogo.dtos.NegocioRequestDTO;
import com.moviles.catalogo.dtos.NegocioResponseDTO;
import com.moviles.catalogo.dtos.NegocioUpdateDTO;
import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.services.NegocioService;
import com.moviles.catalogo.security.JwtService;
import com.moviles.catalogo.security.NegocioPrincipal;
import com.moviles.catalogo.exceptions.ForbiddenException;
import com.moviles.catalogo.exceptions.BusinessException;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

    private final NegocioService negocioService;
    private final JwtService jwtService;

    public NegocioController(NegocioService negocioService, JwtService jwtService){
        this.negocioService = negocioService;
        this.jwtService = jwtService;
    }

    // -------------------------------
    // 1️⃣ Crear un negocio (POST)
    // -------------------------------
    @PostMapping("/guardar")
    public ResponseEntity<NegocioResponseDTO> crearNegocio(@Valid @RequestBody NegocioRequestDTO negocio) {
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
    public ResponseEntity<NegocioResponseDTO> actualizarNegocio(
        @PathVariable Long id,
        @Valid @RequestBody NegocioUpdateDTO negocio,
        Authentication authentication
    ) {
        verificarPropietario(id, authentication);
        Negocio actualizado = negocioService.actualizarNegocio(id, negocio);
        return ResponseEntity.ok(aNegocioResponse(actualizado));
    }

    // -------------------------------
    // 4️⃣ Eliminar un negocio (DELETE)
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNegocio(
        @PathVariable Long id,
        Authentication authentication
    ) {
        verificarPropietario(id, authentication);
        negocioService.eliminarNegocio(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // Verificar el login
    // -------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody NegocioLoginDTO loginRequest) {
        Optional<Negocio> negocio = negocioService.login(
            loginRequest.nombreNegocio(),
            loginRequest.passwordNegocio()
        );

        if (negocio.isPresent()) {
            Negocio encontrado = negocio.get();
            String token = jwtService.generateToken(
                encontrado.getIdNegocio(),
                encontrado.getNombreNegocio()
            );
            LoginResponseDTO response = new LoginResponseDTO(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                aNegocioResponse(encontrado)
            );
            return ResponseEntity.ok(response);
        } else {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
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

    private void verificarPropietario(Long id, Authentication authentication) {
        NegocioPrincipal principal = (NegocioPrincipal) authentication.getPrincipal();
        if (principal.idNegocio() != id) {
            throw new ForbiddenException("Solo puedes administrar tu propio negocio");
        }
    }
}
