package com.example.wikifountains;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FuentesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FuenteAdapter adapter;
    private AppDatabase db;
    private String puebloSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recibir el pueblo seleccionado desde InicioActivity
        Intent intent = getIntent();
        puebloSeleccionado = intent.getStringExtra("pueblo");

        // Inicializar la base de datos
        db = AppDatabase.getInstance(this);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFuentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar las fuentes desde la base de datos
        cargarFuentes();
    }

    private void cargarFuentes() {
        // Obtener las fuentes filtradas por pueblo
        List<Fuente> fuentes = db.fuenteDao().getFuentesPorPueblo(puebloSeleccionado);

        // Asignar las fuentes al adaptador
        adapter = new FuenteAdapter(fuentes);
        recyclerView.setAdapter(adapter);
    }
}