package com.example.wikifountains.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.example.wikifountains.data.Fuente;
import com.example.wikifountains.R;

public class DetallesFuenteActivity extends BaseActivity {
    private TextView textViewNombre;
    private TextView textViewLocalidad;
    private TextView textViewCalle;
    private TextView textViewCoordenadas;
    private TextView textViewDescripcion;
    private Fuente fuente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_detalles_fuente);

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
            String coords = fuente.getLatitud() + ", " + fuente.getLongitud();
            textViewCoordenadas.setText(coords);
            textViewDescripcion.setText(fuente.getDescripcion());
        }
    }
}
