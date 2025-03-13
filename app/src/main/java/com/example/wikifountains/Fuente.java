package com.example.wikifountains;


import androidx.room.PrimaryKey;

public class Fuente {
    private int id;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    public Fuente(int id, String nombre, String ubicacion, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
