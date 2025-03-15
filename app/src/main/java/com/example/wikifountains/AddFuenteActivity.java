package com.example.wikifountains;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddFuenteActivity extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextLocalidad;
    private EditText editTextCalle;
    private EditText editTextCoordenadas;
    private EditText editTextDescripcion;
    private Button buttonGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fuente);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextLocalidad= findViewById(R.id.editTextLocalidad);
        editTextLocalidad.setText(getIntent().getStringExtra("localidadseleccionado"));
        editTextCalle = findViewById(R.id.editTextCalle);
        editTextCoordenadas = findViewById(R.id.editTextCoordenadas);
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
        String localidad = editTextLocalidad.getText().toString();
        String calle = editTextCalle.getText().toString();
        String coordenadas = editTextCoordenadas.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();

        Fuente nuevaFuente = new Fuente(nombre, localidad, calle, coordenadas, descripcion);

        AppDatabase db = AppDatabase.getInstance(this);
        db.fuenteDao().insert(nuevaFuente);

        finish(); // Regresa a la actividad anterior
    }
}