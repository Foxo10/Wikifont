package com.example.wikifountains;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FuenteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFuentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Fuente> fuentes = new ArrayList<>();
        fuentes.add(new Fuente("Fuente del Parque", "Bilbao", R.drawable.fuente_bilbo_donacasilda, "Una fuente histórica en el centro de Bilbao."));
        fuentes.add(new Fuente("Fuente de la Plaza", "Getxo", R.drawable.fuente_getxo_plaza, "Fuente moderna con iluminación nocturna."));

        // Crear el adaptador y asignarlo al RecyclerView
        adapter = new FuenteAdapter(fuentes);
        recyclerView.setAdapter(adapter);

    }
}