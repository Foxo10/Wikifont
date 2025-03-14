package com.example.wikifountains;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FuenteDao {
    @Insert
    void insert(Fuente fuente);

    @Query("SELECT * FROM fuentes WHERE ubicacion = :pueblo")
    List<Fuente> getFuentesPorPueblo(String pueblo);

    @Query("SELECT * FROM fuentes")
    List<Fuente> getAllFuentes();
}
