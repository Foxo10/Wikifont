package com.example.wikifountains.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;
import com.example.wikifountains.R;

import java.util.concurrent.Executors;

public class AddFuenteActivity extends BaseActivity {
    private EditText editTextNombre;
    private EditText editTextLocalidad;
    private EditText editTextCalle;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private EditText editTextDescripcion;
    private Button buttonGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_add_fuente);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextLocalidad= findViewById(R.id.editTextLocalidad);
        editTextLocalidad.setText(getIntent().getStringExtra("localidadseleccionado"));
        editTextCalle = findViewById(R.id.editTextCalle);
        editTextLatitud = findViewById(R.id.editTextLatitud);
        editTextLongitud = findViewById(R.id.editTextLongitud);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        buttonGuardar = findViewById(R.id.buttonGuardar);

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFuente();
            }
        });
    }

    private void guardarFuente() {
        String nombre = editTextNombre.getText().toString().trim();
        String localidad = editTextLocalidad.getText().toString().trim();
        String calle = editTextCalle.getText().toString().trim();
        String latitud = editTextLatitud.getText().toString().trim();
        String longitud = editTextLongitud.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || localidad.isEmpty() || calle.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución si algún campo está vacío
        }

        // Crear la nueva fuente
        Fuente nuevaFuente = new Fuente(nombre, localidad, calle, latitud, longitud, descripcion);

        // Guardar la fuente en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).fuenteDao().insert(nuevaFuente);
            runOnUiThread(() -> {
                Toast.makeText(this, "Fuente guardada correctamente", Toast.LENGTH_SHORT).show();
                finish(); // Cerrar la actividad después de guardar
            });
        });
    }
}