package com.example.wikifountains;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

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

        String[] pueblos = {"Bilbao", "Getxo", "Portugalete", "Barakaldo", "Durango", "Gernika", "Bermeo", "Mungia"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pueblos);
        listViewPueblos.setAdapter(adapter);

        listViewPueblos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String puebloSeleccionado = pueblos[position];
                filtrarFuentesPorPueblo(puebloSeleccionado);
            }
        });

    }

    private void filtrarFuentesPorPueblo(String pueblo) {
        Intent intent = new Intent(InicioActivity.this, FuentesActivity.class);
        intent.putExtra("pueblo", pueblo);
        startActivity(intent);
    }
}