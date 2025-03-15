package com.example.wikifountains;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddFuenteActivity extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextUbicacion;
    private EditText editTextDescripcion;
    private Button buttonGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fuente);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextUbicacion = findViewById(R.id.editTextUbicacion);
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
        String nombre = editTextNombre.getText().toString();
        String ubicacion = editTextUbicacion.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();

        Fuente nuevaFuente = new Fuente();
        nuevaFuente.setNombre(nombre);
        nuevaFuente.setUbicacion(ubicacion);
        nuevaFuente.setDescripcion(descripcion);

        AppDatabase db = AppDatabase.getInstance(this);
        db.fuenteDao().insert(nuevaFuente);

        finish(); // Regresa a la actividad anterior
    }
}