package com.example.wikifountains;

public class Fuente {
    private String nombre;
    private String ubicacion;
    private int imagen;  // ID del recurso de la imagen (drawable)
    private String descripcion;  // Opcional: descripción de la fuente

    // Constructor
    public Fuente(String nombre, String ubicacion, int imagen, String descripcion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public int getImagen() {
        return imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }
}