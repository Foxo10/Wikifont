package com.example.wikifountains;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {
    private ListView listViewPueblos;
    private Button buttonOpciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Inicializar vistas
        listViewPueblos = findViewById(R.id.listViewPueblos);
        buttonOpciones = findViewById(R.id.buttonOpciones);

        List<String> localidades = List.of("Bilbao", "Getxo", "Portugalete", "Barakaldo", "Durango", "Gernika", "Bermeo", "Mungia", "Sopelana", "Berango");
        Collator collator = Collator.getInstance(new Locale("es"));
        List<String> sortedLocalidades = new ArrayList<>(localidades);
        sortedLocalidades.sort(collator);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortedLocalidades);
        listViewPueblos.setAdapter(adapter);

        listViewPueblos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String localidadSeleccionada = sortedLocalidades.get(position);
                filtrarFuentesPorLocalidades(localidadSeleccionada);
                Log.d("tag 1", "Localidad seleccionado: " + localidadSeleccionada);
            }
        });

    }

    private void filtrarFuentesPorLocalidades(String localidad) {
        Intent intent = new Intent(InicioActivity.this, FuentesActivity.class);
        intent.putExtra("localidad", localidad);
        Log.d("tag 2", "Iniciando FuentesActivity con la localidad: " + localidad);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}