package com.moviles.catalogo.services;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import com.moviles.catalogo.dtos.NegocioRequestDTO;
import com.moviles.catalogo.dtos.NegocioUpdateDTO;
import com.moviles.catalogo.entities.Negocio;
import com.moviles.catalogo.exceptions.BusinessException;
import com.moviles.catalogo.exceptions.ResourceNotFoundException;
import com.moviles.catalogo.repositories.NegocioRepository;

@Service
public class NegocioServiceImpl implements NegocioService{

    private final NegocioRepository negocioRepository;
    private final PasswordEncoder passwordEncoder;

    public NegocioServiceImpl(
        @NonNull NegocioRepository negocio,
        @NonNull PasswordEncoder passwordEncoder
    ){
        this.negocioRepository = negocio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Negocio guardarNegocio(@NonNull NegocioRequestDTO dto) {
        if (negocioRepository.findByNombreNegocio(dto.nombreNegocio()).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un negocio con ese nombre");
        }
        Negocio negocio = new Negocio();
        negocio.setNombreNegocio(dto.nombreNegocio());
        negocio.setDireccionNegocio(dto.direccionNegocio());
        negocio.setTelefonoNegocio(dto.telefonoNegocio());
        negocio.setFotoNegocio(dto.fotoNegocio());
        negocio.setPasswordNegocio(passwordEncoder.encode(dto.passwordNegocio()));
        return negocioRepository.save(negocio);
    }

    @Override
    public Optional<Negocio> obtenerPorId(@NonNull Long id) {
        return negocioRepository.findById(id);
    }

    @Override
    public Negocio actualizarNegocio(@NonNull Long id, NegocioUpdateDTO dto) {
        return negocioRepository.findById(id)
            .map(existing -> {
                if (dto.nombreNegocio() != null && !dto.nombreNegocio().isBlank()) {
                    if (!existing.getNombreNegocio().equals(dto.nombreNegocio())
                        && negocioRepository.findByNombreNegocio(dto.nombreNegocio()).isPresent()) {
                        throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un negocio con ese nombre");
                    }
                    existing.setNombreNegocio(dto.nombreNegocio());
                }
                if (dto.direccionNegocio() != null) {
                    existing.setDireccionNegocio(dto.direccionNegocio());
                }
                if (dto.fotoNegocio() != null) {
                    existing.setFotoNegocio(dto.fotoNegocio());
                }
                if (dto.telefonoNegocio() != null) {
                    existing.setTelefonoNegocio(dto.telefonoNegocio());
                }
                if (dto.passwordNegocio() != null && !dto.passwordNegocio().isBlank()) {
                    existing.setPasswordNegocio(passwordEncoder.encode(dto.passwordNegocio()));
                }
                return negocioRepository.save(existing);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado"));
    }

    @Override
    public Negocio actualizarLogo(@NonNull Long id, @NonNull String fotoNegocio) {
        Negocio negocio = negocioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado"));
        negocio.setFotoNegocio(fotoNegocio);
        return negocioRepository.save(negocio);
    }

    @Override
    public void eliminarNegocio(@NonNull Long id) {
        negocioRepository.deleteById(id);
    }

    @Override
    public Optional<Negocio> login(@NonNull String nombreNegocio, String password) {
        Optional<Negocio> negocioOpt = negocioRepository.findByNombreNegocio(nombreNegocio);
        if (negocioOpt.isPresent()
            && passwordEncoder.matches(password, negocioOpt.get().getPasswordNegocio())) {
            return negocioOpt;
        }
        return Optional.empty();
    }
}
