package com.example.wikifountains;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FuenteDao {
    @Insert
    void insert(Fuente fuente);

    @Insert
    void insert(List<Fuente> fuentes);
    @Update
    void update(Fuente fuente);

    @Query("SELECT * FROM fuentes WHERE localidad = :localidad")
    List<Fuente> getFuentesPorLocalidad(String localidad);
    @Query("SELECT * FROM fuentes WHERE id = :id")
    Fuente getFuenteById(int id);

    @Query("SELECT * FROM fuentes")
    List<Fuente> getAllFuentes();

    @Delete
    void deleteFuente(Fuente fuente);

    @Query("SELECT COUNT(*) FROM fuentes")
    int countFuentes();

}