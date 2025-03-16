package com.example.wikifountains;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {
    private static final String LANGUAGE_KEY = "language";
    private static final String PREFS_NAME = "MyPrefs";
    private ListView listViewPueblos;
    private Button buttonOpciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_inicio);

        // Inicializar vistas
        listViewPueblos = findViewById(R.id.listViewPueblos);
        buttonOpciones = findViewById(R.id.buttonOpciones);

        // Cargar localidades desde el fichero
        List<String> localidades = cargarLocalidadesDesdeFichero();

        // Cargar la preferencia de ordenar alfabéticamente
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean ordenarAlfabeticamente = preferences.getBoolean("ordenar_fuentes", false);

        // Ordenar las localidades alfabéticamente si la preferencia está activada
        if (ordenarAlfabeticamente) {
            Collator collator = Collator.getInstance(new Locale("es"));
            localidades.sort(collator);
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

        // Manejar clic en el botón de opciones
        buttonOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this, OptionsActivity.class);
                startActivityForResult(intent, 1); // Usar startActivityForResult
            }
        });
    }

    private void filtrarFuentesPorLocalidades(String localidad) {
        Intent intent = new Intent(InicioActivity.this, FuentesActivity.class);
        intent.putExtra("localidad", localidad);
        Log.d("tag 2", "Iniciando FuentesActivity con la localidad: " + localidad);
        startActivity(intent);
    }
    private void loadLocale() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = preferences.getString(LANGUAGE_KEY, "es");
        if (!language.isEmpty()) {
            setLocale(language);
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            recreate();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}