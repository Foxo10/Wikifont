package com.example.wikifountains;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.Executors;

public class FuentesActivity extends AppCompatActivity implements EliminarFuenteDialog.EliminarFuenteListener {
    private RecyclerView recyclerView;
    private FuenteAdapter adapter;
    private AppDatabase db;
    private String localidadSeleccionado;
    private Button buttonAddFuente;
    private TextView textoFuentesDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recibir el pueblo seleccionado desde InicioActivity
        Intent intent = getIntent();
        localidadSeleccionado = intent.getStringExtra("localidad");
        textoFuentesDe = findViewById(R.id.textViewFuentesDe);
        Log.d("tag 3", "Localidad seleccionada: " + localidadSeleccionado);
        textoFuentesDe.setText("Fuentes de " + localidadSeleccionado);

        // Inicializar la base de datos
        db = AppDatabase.getInstance(this);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFuentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar y configurar botón de añadir fuente
        buttonAddFuente = findViewById(R.id.buttonAddFuente);
        buttonAddFuente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FuentesActivity.this, AddFuenteActivity.class);
                intent.putExtra("localidadseleccionado", localidadSeleccionado);
                startActivity(intent);
            }
        });

        // Cargar las fuentes desde la base de datos
        cargarFuentes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFuentes();
    }

    private void cargarFuentes() {
        // Obtener las fuentes filtradas por pueblo
        Log.d("tag 4", "Cargando fuentes para la localidad: " + localidadSeleccionado);
        List<Fuente> fuentes = db.fuenteDao().getFuentesPorLocalidad(localidadSeleccionado);
        Log.d("tag 5", "Número de fuentes encontradas: " + (fuentes != null ? fuentes.size() : "null"));

        // Asignar las fuentes al adaptador
        adapter = new FuenteAdapter(fuentes); // Constructor simplificado
        adapter.setEliminarFuenteListener(this); // Asignar el listener
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEliminarConfirmado(Fuente fuente) {
        // Eliminar la fuente de la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            db.fuenteDao().deleteFuente(fuente);
            runOnUiThread(() -> {
                cargarFuentes(); // Recargar las fuentes después de eliminar
            });
        });
    }
}