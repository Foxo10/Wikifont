package com.example.wikifountains;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class EditFuenteActivity extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextLocalidad;
    private EditText editTextCalle;
    private EditText editTextCoordenadas;
    private EditText editTextDescripcion;
    private Button buttonGuardar;
    private Fuente fuente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fuente);

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextLocalidad = findViewById(R.id.editTextLocalidad);
        editTextCalle = findViewById(R.id.editTextCalle);
        editTextCoordenadas = findViewById(R.id.editTextCoordenadas);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        buttonGuardar = findViewById(R.id.buttonGuardar);

        // Obtener la fuente a editar desde el Intent
        fuente = (Fuente) getIntent().getSerializableExtra("fuente");
        if (fuente != null) {
            // Cargar los datos de la fuente en los EditText
            editTextNombre.setText(fuente.getNombre());
            editTextLocalidad.setText(fuente.getLocalidad());
            editTextCalle.setText(fuente.getCalle());
            editTextCoordenadas.setText(fuente.getCoordenadas());
            editTextDescripcion.setText(fuente.getDescripcion());
        }

        // Guardar cambios
        buttonGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        // Obtener los nuevos valores de los EditText
        String nombre = editTextNombre.getText().toString();
        String localidad = editTextLocalidad.getText().toString();
        String calle = editTextCalle.getText().toString();
        String coordenadas = editTextCoordenadas.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();

        // Actualizar la fuente
        fuente.setNombre(nombre);
        fuente.setLocalidad(localidad);
        fuente.setCalle(calle);
        fuente.setCoordenadas(coordenadas);
        fuente.setDescripcion(descripcion);

        // Guardar los cambios en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).fuenteDao().update(fuente);
            runOnUiThread(() -> {
                finish(); // Cerrar la actividad después de guardar
            });
        });
    }
}
