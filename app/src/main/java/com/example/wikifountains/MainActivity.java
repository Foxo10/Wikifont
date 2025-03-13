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

        recyclerView = findViewById(R.id.recyclerViewFuentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Fuente> fuentes = new ArrayList<>();
        fuentes.add(new Fuente(1, "Fuente del Parque", "Bilbao", "Una fuente histórica en el centro de Bilbao."));
        fuentes.add(new Fuente(2, "Fuente de la Plaza", "Getxo", "Fuente moderna con iluminación nocturna."));

        adapter = new FuenteAdapter(fuentes);
        recyclerView.setAdapter(adapter);

    }
}