package com.example.wikifountains.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.wikifountains.R;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class BBDDInitializer {
    public static void initialize(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);

            List<Fuente> fuentes = cargarFuentesDesdeCSV(context);
            for (Fuente fuente : fuentes) {
                Fuente existente = db.fuenteDao().getFuenteByNombre(fuente.getNombre());
                if (existente == null) {
                    db.fuenteDao().insert(fuente);
                }
            }
        });
    }
    private static List<Fuente> cargarFuentesDesdeCSV(Context context) {
        List<Fuente> fuentes = new ArrayList<>();
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.fuentes);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(inputStreamReader)) {
            String[] nextLine;
            boolean isFirstLine = true;
            while ((nextLine = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String nombre = nextLine[0];
                String localidad = nextLine[1];
                String calle = nextLine[2];
                float latitud = Float.parseFloat(nextLine[3]);
                float longitud = Float.parseFloat(nextLine[4]);
                String descripcion = nextLine[5];

                Fuente fuente = new Fuente(nombre, localidad, calle, latitud, longitud, descripcion);
                fuentes.add(fuente);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return fuentes;
    }

}
