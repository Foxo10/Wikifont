package com.example.wikifountains.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wikifountains.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PueblosActivity extends BaseActivity {

    private ListView listViewPueblos;

    private ImageView imagenMapa;
    private ImageView imagenLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_pueblos);

        // Inicializar vistas
        listViewPueblos = findViewById(R.id.listViewPueblos);


        // Cargar localidades desde el fichero
        List<String> localidades = cargarLocalidadesDesdeFichero();

        // Cargar la preferencia de ordenar alfabéticamente
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean ordenarAlfabeticamente = preferences.getBoolean("ordenar_fuentes", false);

        // Ordenar las localidades alfabéticamente si la preferencia está activada
        if (!ordenarAlfabeticamente) {
            // Orden alfabético normal (A-Z)
            Collator collator = Collator.getInstance(new Locale("es"));
            localidades.sort(collator);
        } else {
            // Orden alfabético inverso (Z-A)
            Collator collator = Collator.getInstance(new Locale("es"));
            localidades.sort(collator.reversed()); // <- reversed() aquí
        }

        // Configurar el adaptador para el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, localidades);
        listViewPueblos.setAdapter(adapter);

        // Manejar clics en las localidades
        listViewPueblos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String localidadSeleccionada = localidades.get(position);
                filtrarFuentesPorLocalidades(localidadSeleccionada);
                Log.d("tag 1", "Localidad seleccionada: " + localidadSeleccionada);
            }
        });

        // Configurar imagen de uribe kosta
        int[] imageIDs = {
                R.drawable.mapa_uribe_kosta,
                R.drawable.uribe_kosta_colores,
                R.drawable.bizakaia_uribe_kosta
        };
        imagenMapa = findViewById(R.id.imageView);
        final int[] currentIndex = {0};
        imagenMapa.setImageResource(imageIDs[currentIndex[0]]);

        imagenMapa.setOnClickListener(v -> {
            currentIndex[0] = (currentIndex[0] + 1) % imageIDs.length;
            imagenMapa.setImageResource(imageIDs[currentIndex[0]]);
        });

        // Configurar imagen logo
        imagenLogo = findViewById(R.id.logoImagen);
        imagenLogo.setImageResource(R.drawable.wikifont_logo);
    }

    private void filtrarFuentesPorLocalidades(String localidad) {
        Intent intent = new Intent(PueblosActivity.this, FuentesActivity.class);
        intent.putExtra("localidad", localidad);
        Log.d("tag 2", "Iniciando FuentesActivity con la localidad: " + localidad);
        startActivity(intent);
    }
    private List<String> cargarLocalidadesDesdeFichero() {
        List<String> localidades = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(R.raw.localidades);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                localidades.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return localidades;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}