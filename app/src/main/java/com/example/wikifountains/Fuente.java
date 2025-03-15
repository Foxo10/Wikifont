package com.example.wikifountains;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fuentes")
public class Fuente {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombre;
    private String ubicacion;
    private String descripcion;

    // Constructor
    public Fuente(String nombre, String ubicacion, String descripcion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}