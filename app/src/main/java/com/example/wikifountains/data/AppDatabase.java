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
import java.util.concurrent.Executors;

@Database(entities = {Fuente.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract FuenteDao fuenteDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            // Eliminar la base de datos existente
            //deleteDatabase(context);
            context.deleteDatabase("fuentes_database");

            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "fuentes_database")
                    .allowMainThreadQueries()  // Permitir consultas en el hilo principal (solo para pruebas)
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                // Solo insertar si la tabla está vacía
                                if (instance.fuenteDao().countFuentes() == 0) {
                                    instance.fuenteDao().insert(PREPOPULATE_DATA);
                                    Log.d(TAG, "Datos iniciales insertados");
                                }
                            });
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
            new Fuente("Fuente de Berango Antzokia", "Berango", "9 Abarotxu Bidea", 43.35531045006519, -2.9954482967626492, "Fuente ubicada en los columpios previos a la entrada del Antzoki."),
            new Fuente("fuente agua potable Algorta", "Getxo", "Telletxe Kalea, 3,", 43.351172132492216, -3.009839010952571,  "Fuente de agua potable en el parque de la estación de metro de Algorta.")
            );

}