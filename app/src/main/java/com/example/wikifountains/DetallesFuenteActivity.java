package com.example.wikifountains;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetallesFuenteActivity extends AppCompatActivity {
    private TextView textViewNombre;
    private TextView textViewLocalidad;
    private TextView textViewCalle;
    private TextView textViewCoordenadas;
    private TextView textViewDescripcion;
    private Fuente fuente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_fuente);

        // Inicializar vistas
        textViewNombre = findViewById(R.id.textViewNombre);
        textViewLocalidad = findViewById(R.id.textViewLocalidad);
        textViewCalle = findViewById(R.id.textViewCalle);
        textViewCoordenadas = findViewById(R.id.textViewCoordenadas);
        textViewDescripcion = findViewById(R.id.textViewDescripcion);

        // Obtener la fuente desde el Intent
        fuente = (Fuente) getIntent().getSerializableExtra("fuente");
        if (fuente != null) {
            // Mostrar los detalles de la fuente
            textViewNombre.setText(fuente.getNombre());
            textViewLocalidad.setText(fuente.getLocalidad());
            textViewCalle.setText(fuente.getCalle());
            textViewCoordenadas.setText(fuente.getCoordenadas());
            textViewDescripcion.setText(fuente.getDescripcion());
        }
    }
}
