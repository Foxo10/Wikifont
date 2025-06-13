package com.example.wikifountains.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;
import com.example.wikifountains.R;

import java.util.concurrent.Executors;

public class EditFuenteActivity extends BaseActivity {
    private EditText editTextNombre;
    private EditText editTextLocalidad;
    private EditText editTextCalle;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private EditText editTextDescripcion;
    private Button buttonGuardar;
    private Fuente fuente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_edit_fuente);

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextLocalidad = findViewById(R.id.editTextLocalidad);
        editTextCalle = findViewById(R.id.editTextCalle);
        editTextLatitud = findViewById(R.id.editTextLatitud);
        editTextLongitud = findViewById(R.id.editTextLongitud);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        buttonGuardar = findViewById(R.id.buttonGuardar);

        // Obtener la fuente a editar desde el Intent
        fuente = (Fuente) getIntent().getSerializableExtra("fuente");
        if (fuente != null) {
            // Cargar los datos de la fuente en los EditText
            editTextNombre.setText(fuente.getNombre());
            editTextLocalidad.setText(fuente.getLocalidad());
            editTextCalle.setText(fuente.getCalle());
            editTextLatitud.setText(String.valueOf(fuente.getLatitud()));
            editTextLongitud.setText(String.valueOf(fuente.getLongitud()));
            editTextDescripcion.setText(fuente.getDescripcion());
        }

        // Guardar cambios
        buttonGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        String nombre = editTextNombre.getText().toString().trim();
        String localidad = editTextLocalidad.getText().toString().trim();
        String calle = editTextCalle.getText().toString().trim();
        String latitudStr = editTextLatitud.getText().toString().trim();
        String longitudStr = editTextLongitud.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || localidad.isEmpty() || calle.isEmpty() || latitudStr.isEmpty() || longitudStr.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, R.string.toast_fields, Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución si algún campo está vacío
        }

        // Actualizar la fuente
        double latitud = Double.parseDouble(latitudStr);
        double longitud = Double.parseDouble(longitudStr);

        fuente.setNombre(nombre);
        fuente.setLocalidad(localidad);
        fuente.setCalle(calle);
        fuente.setLatitud(latitud);
        fuente.setLongitud(longitud);
        fuente.setDescripcion(descripcion);

        // Guardar los cambios en la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).fuenteDao().update(fuente);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
                finish(); // Cerrar la actividad después de guardar
            });
        });
    }
}
