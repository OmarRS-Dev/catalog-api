package com.moviles.catalogo.services;

import java.util.Optional;

import com.moviles.catalogo.dtos.NegocioRequestDTO;
import com.moviles.catalogo.dtos.NegocioUpdateDTO;
import com.moviles.catalogo.entities.Negocio;

public interface NegocioService {

    Negocio guardarNegocio(NegocioRequestDTO negocio);
    Optional<Negocio> obtenerPorId( Long id);
    Negocio actualizarNegocio(Long id, NegocioUpdateDTO negocio);
    Negocio actualizarLogo(Long id, String fotoNegocio);
    void eliminarNegocio( Long id);
    Optional<Negocio> login(String nombreNegocio, String password);
}
