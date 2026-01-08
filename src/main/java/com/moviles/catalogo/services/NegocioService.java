package com.moviles.catalogo.services;

import java.util.Optional;

import org.springframework.lang.NonNull;

import com.moviles.catalogo.entities.Negocio;

public interface NegocioService {

    Negocio guardarNegocio(@NonNull Negocio negocio);
    Optional<Negocio> obtenerPorId(@NonNull Long id);
    Negocio actualizarNegocio(@NonNull Long id, Negocio negocio);
    void eliminarNegocio(@NonNull Long id);
    Optional<Negocio> login(@NonNull String nombreNegocio, String password);
}
