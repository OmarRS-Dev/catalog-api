package com.moviles.catalogo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    private String nombreProducto;

    private String descripcionProducto;

    // Relación con Categoria
    @ManyToOne
    @JoinColumn(name = "idCategoria") 
    private Categoria categoria;

    // Relación con Negocio
    @ManyToOne
    @JoinColumn(name = "IdNegocio") 
    private Negocio negocio;

    private String portadaProducto;

    private double precioProducto;

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    public String getPortadaProducto() {
        return portadaProducto;
    }

    public void setPortadaProducto(String portadaProducto) {
        this.portadaProducto = portadaProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

   
}
