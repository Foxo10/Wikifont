package com.example.wikifountains.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wikifountains.R;

import java.util.Locale;

public class OptionsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String LANGUAGE_KEY = "language";
    private static final String KEY_ORDENAR = "ordenar_fuentes";
    private Switch switchOrdenar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Button buttonEnglish = findViewById(R.id.button_english);
        Button buttonSpanish = findViewById(R.id.button_spanish);
        Button buttonBasque = findViewById(R.id.button_basque);

        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });

        buttonSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("es");
            }
        });

        buttonBasque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("eu");
            }
        });

        switchOrdenar = findViewById(R.id.switchOrdenar);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean ordenarFuentes = preferences.getBoolean(KEY_ORDENAR, false);
        switchOrdenar.setChecked(ordenarFuentes);

        // Guardar la preferencia cuando el usuario cambie el estado del Switch
        switchOrdenar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_ORDENAR, isChecked);
            editor.apply();
            Toast.makeText(this, "Preferencia de orden alfabético guardada", Toast.LENGTH_SHORT).show();
            // Devolver un resultado para indicar que se debe recargar la actividad
            setResult(RESULT_OK);
        });

    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());

        // Guardar el idioma seleccionado
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();

        // Recargar la actividad principal para aplicar los cambios
        Intent intent = new Intent(this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
