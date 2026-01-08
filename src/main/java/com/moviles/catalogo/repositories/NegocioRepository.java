package com.moviles.catalogo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moviles.catalogo.entities.Negocio;
import java.util.Optional;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Long> {
    Optional<Negocio> findByNombreNegocio(String NombreNegocio);
}
