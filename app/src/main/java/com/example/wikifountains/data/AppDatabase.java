package com.example.wikifountains.data;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Database(entities = {Fuente.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract FuenteDao fuenteDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            // Eliminar la base de datos existente
            // deleteDatabase(context);

            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "fuentes_database")
                    .allowMainThreadQueries()  // Permitir consultas en el hilo principal (solo para pruebas)
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Insertar datos iniciales en un hilo separado
                            new Thread(() -> {
                                instance.fuenteDao().insert(PREPOPULATE_DATA);
                                Log.d(TAG, "Datos iniciales insertados");
                            }).start();
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static void deleteDatabase(Context context) {
        try {
            File database = context.getDatabasePath("fuentes_database");
            if (database.exists()) {
                database.delete();
                Log.d(TAG, "Base de datos eliminada");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar la base de datos: ", e);
        }
    }

    // Lista de fuentes predeterminadas (sin imágenes)
    private static final List<Fuente> PREPOPULATE_DATA = Arrays.asList(
            new Fuente("Fuente de Neguri", "Getxo", "12 Av. Algortako Etorbidea", "", ""),
            new Fuente("Fuente plaza del casino", "Getxo", "49 Av. Basagoiti", "", ""),
            new Fuente("Fuente de la playa", "Sopelana", "Av. Atxabiribil, 77", "", ""),
            new Fuente("Fuente de Zabalbide", "Bilbao", "Zabalbide Kalea, 5, Ibaiondo", "",""),
            new Fuente("Fuente del perro", "Bilbao","Txakur Kalea, 2-4, Ibaiondo","","Fuente de estilo neoclásico del 1800 adosada a una pared de la calle del Perro. Tiene tres caños y un poyo en el que descansar después de refrescarnos.")
            );

}