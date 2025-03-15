package com.example.wikifountains;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FuentesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FuenteAdapter adapter;
    private AppDatabase db;
    private String puebloSeleccionado;
    private Button buttonAddFuente;
    private TextView textoFuentesDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recibir el pueblo seleccionado desde InicioActivity
        Intent intent = getIntent();
        puebloSeleccionado = intent.getStringExtra("pueblo");
        textoFuentesDe = findViewById(R.id.textViewFuentesDe);
        textoFuentesDe.setText("Fuentes de "+ puebloSeleccionado);

        // Inicializar la base de datos
        db = AppDatabase.getInstance(this);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFuentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar y configurar boton de añadir fuente
        buttonAddFuente = findViewById(R.id.buttonAddFuente);
        buttonAddFuente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FuentesActivity.this, AddFuenteActivity.class);
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
        List<Fuente> fuentes = db.fuenteDao().getFuentesPorPueblo(puebloSeleccionado);

        // Asignar las fuentes al adaptador
        adapter = new FuenteAdapter(fuentes);
        recyclerView.setAdapter(adapter);
    }
}