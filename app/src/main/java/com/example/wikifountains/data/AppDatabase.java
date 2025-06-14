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

@Database(entities = {Fuente.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract FuenteDao fuenteDao();

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                            AppDatabase.class, "mifuente_db")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }

    private static void deleteDatabase(Context context) {
        try {
            File database = context.getDatabasePath("mifuente_db");
            if (database.exists()) {
                database.delete();
                Log.d(TAG, "Base de datos eliminada");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar la base de datos: ", e);
        }
    }


}