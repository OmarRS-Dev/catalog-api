package com.moviles.catalogo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Negocios")
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idNegocio;

    private String nombreNegocio;

    private String direccionNegocio;

    private String telefonoNegocio;

    private String fotoNegocio;

    private String passwordNegocio;

    // Getters y Setters
    public long getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(long idNegocio) {
        this.idNegocio = idNegocio;
    }

    public String getNombreNegocio() {
        return nombreNegocio;
    }

    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public String getDireccionNegocio() {
        return direccionNegocio;
    }

    public void setDireccionNegocio(String direccionNegocio) {
        this.direccionNegocio = direccionNegocio;
    }

    public String getTelefonoNegocio() {
        return telefonoNegocio;
    }

    public void setTelefonoNegocio(String telefonoNegocio) {
        this.telefonoNegocio = telefonoNegocio;
    }

    public String getFotoNegocio() {
        return fotoNegocio;
    }

    public void setFotoNegocio(String fotoNegocio) {
        this.fotoNegocio = fotoNegocio;
    }

    public String getPasswordNegocio() {
        return passwordNegocio;
    }

    public void setPasswordNegocio(String passwordNegocio) {
        this.passwordNegocio = passwordNegocio;
    }
}

