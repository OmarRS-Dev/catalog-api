package com.moviles.catalogo.services;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.repositories.NegocioRepository;

@Service
public class NegocioServiceImpl implements NegocioService{

    private NegocioRepository negocioRepository;

    public NegocioServiceImpl(@NonNull NegocioRepository negocio){
        this.negocioRepository = negocio;
    }

    @Override
    public Negocio guardarNegocio(@NonNull Negocio negocio) {
        return negocioRepository.save(negocio);
    }

    @Override
    public Optional<Negocio> obtenerPorId(@NonNull Long id) {
        return negocioRepository.findById(id);
    }

    @Override
    public Negocio actualizarNegocio(@NonNull Long id, Negocio negocio) {
        return negocioRepository.findById(id)
            .map(existing -> {
                existing.setNombreNegocio(negocio.getNombreNegocio());
                existing.setDireccionNegocio(negocio.getDireccionNegocio());
                existing.setFotoNegocio(negocio.getFotoNegocio());
                existing.setPasswordNegocio(negocio.getPasswordNegocio());
                existing.setTelefonoNegocio(negocio.getTelefonoNegocio());
                return negocioRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
    }

    @Override
    public void eliminarNegocio(@NonNull Long id) {
        negocioRepository.deleteById(id);
    }

    @Override
    public Optional<Negocio> login(@NonNull String nombreNegocio, String password) {
        Optional<Negocio> negocioOpt = negocioRepository.findByNombreNegocio(nombreNegocio);
        if (negocioOpt.isPresent() && negocioOpt.get().getPasswordNegocio().equals(password)) {
            return negocioOpt;
        }
        return Optional.empty();
    }
}
