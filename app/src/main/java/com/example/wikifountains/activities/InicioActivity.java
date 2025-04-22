package com.example.wikifountains.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.wikifountains.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class InicioActivity extends AppCompatActivity {
    private static final String LANGUAGE_KEY = "language";
    private static final String PREFS_NAME = "MyPrefs";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    public static final int LANGUAGE_ENGLISH = 1;
    public static final int LANGUAGE_SPANISH = 2;
    public static final int LANGUAGE_BASQUE = 3;
    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        applyTheme();
        
        setContentView(R.layout.activity_inicio);

        initViews();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.syncState();

        setupNavigationDrawer();

        // Cargar la preferencia de ordenar alfabéticamente
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean ordenarAlfabeticamente = preferences.getBoolean("ordenar_fuentes", false);

    }

    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Usar la Toolbar de AndroidX

        // Configurar el ActionBarDrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView elnavigation = findViewById(R.id.nav_view);
        elnavigation.inflateHeaderView(R.layout.nav_header_main);
        elnavigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ajustes:
                                // Acción para "Ajustes"
                                Intent optionsIntent = new Intent(getApplicationContext(), OptionsActivity.class);
                                startActivityForResult(optionsIntent, 1);
                                break;
                            case R.id.localizacion:
                                // Acción para "Mapa"
                                // Crear un intent para abrir Google Maps y buscar fuentes
                                Uri gmmIntentUri1 = Uri.parse("geo:0,0?q=fuentes");
                                //Uri gmmIntentUri2 = Uri.parse("google.streetview:cbll=0,0");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri1);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
                if (elmenudesplegable.isDrawerOpen(GravityCompat.START)) {
                    elmenudesplegable.closeDrawer(GravityCompat.START);
                }
                else{
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        int id=item.getItemId();
        if (this instanceof InicioActivity && id == R.id.action_home){
            return true;
        }
        switch (id){
            case R.id.action_home:{
                intent = new Intent(this, InicioActivity.class);
                // Limpiar el stack de activities si vamos al home
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                recreate();
                startActivity(intent);
                finish();
            }
            case R.id.action_search:{
                intent = new Intent(this, PueblosActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadLocale() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = preferences.getString(LANGUAGE_KEY, "es");
        if (!language.isEmpty()) {
            setLocale(language);
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    protected void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", THEME_SYSTEM);

        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_WikiFountains_Light);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                setTheme(R.style.Theme_WikiFountains);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                setTheme(R.style.Base_Theme_WikiFountains);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inicio_menu,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
}