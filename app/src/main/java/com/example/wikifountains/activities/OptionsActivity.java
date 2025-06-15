package com.example.wikifountains.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wikifountains.R;

import java.util.Locale;

public class OptionsActivity extends BaseActivity {

    private static final String KEY_ORDENAR = "ordenar_fuentes";
    private Switch switchOrdenar;
    private RadioGroup radioGroupTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_options);

        Button buttonEnglish = findViewById(R.id.button_english);
        Button buttonSpanish = findViewById(R.id.button_spanish);
        Button buttonBasque = findViewById(R.id.button_basque);
        radioGroupTheme = findViewById(R.id.radioGroupTheme);

        SharedPreferences themePrefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int themeMode = themePrefs.getInt("theme_mode", THEME_SYSTEM);
        if (themeMode == THEME_LIGHT) {
            radioGroupTheme.check(R.id.radio_light);
        } else if (themeMode == THEME_DARK) {
            radioGroupTheme.check(R.id.radio_dark);
        } else if (themeMode == THEME_FONT) {
            radioGroupTheme.check(R.id.radio_font);
        }

        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int mode = THEME_LIGHT;
            if (checkedId == R.id.radio_dark) {
                mode = THEME_DARK;
            } else if (checkedId == R.id.radio_font) {
                mode = THEME_FONT;
            }
            SharedPreferences.Editor editor = themePrefs.edit();
            editor.putInt("theme_mode", mode);
            editor.apply();
            Toast.makeText(this, getString(R.string.toast_theme_applied), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            recreate();
        });

        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLocale("en");
            }
        });

        buttonSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLocale("es");
            }
        });

        buttonBasque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLocale("eu");
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
            if(isChecked) {
                switchOrdenar.setText(R.string.ordenar_popu);
                Toast.makeText(this, getString(R.string.ordenar_popu) + " " + getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }
            else {
                switchOrdenar.setText(R.string.ordenar_alfa);
                Toast.makeText(this, getString(R.string.ordenar_alfa) + " " + getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }

            // Devolver un resultado para indicar que se debe recargar la actividad
            setResult(RESULT_OK);
        });

    }

    private void changeLocale(String languageCode) {
        setLocale(languageCode);
        // Guardar el idioma seleccionado
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();

        // Recargar la actividad para aplicar los cambios
        Toast.makeText(this, getString(R.string.toast_language_applied), Toast.LENGTH_SHORT).show();
        recreate();

    }

}
