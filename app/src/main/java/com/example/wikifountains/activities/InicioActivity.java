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

public class InicioActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentViewWithDrawer(R.layout.activity_inicio);
        // Cargar orden de preferencia
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        preferences.getBoolean("ordenar_fuentes", false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        if (this instanceof InicioActivity && id == R.id.action_home) {
            return true;
        }
        switch (id) {
            case R.id.action_home:
                intent = new Intent(this, InicioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                recreate();
                startActivity(intent);
                finish();
                break;
            case R.id.action_search:
                intent = new Intent(this, PueblosActivity.class);
                startActivity(intent);
                return true;
            case R.id.localizacion:
                Uri gmmIntentUri1 = Uri.parse("geo:0,0?q=fuentes");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri1);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.inicio_menu, menu);
        return true;
    }


}
