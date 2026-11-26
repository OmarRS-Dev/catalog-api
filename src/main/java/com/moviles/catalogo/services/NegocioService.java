package com.moviles.catalogo.services;

import java.util.Optional;


import com.moviles.catalogo.entities.Negocio;

public interface NegocioService {

    Negocio guardarNegocio(Negocio negocio);
    Optional<Negocio> obtenerPorId( Long id);
    Negocio actualizarNegocio( Long id, Negocio negocio);
    void eliminarNegocio( Long id);
    Optional<Negocio> login(String nombreNegocio, String password);
}
