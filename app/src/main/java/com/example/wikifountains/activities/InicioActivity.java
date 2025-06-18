package com.example.wikifountains.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.example.wikifountains.R;
import com.example.wikifountains.data.BBDDInitializer;
import com.example.wikifountains.data.UserManager;


public class InicioActivity extends BaseActivity {
    private Button buttonLogin;
    private Button buttonRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentViewWithDrawer(R.layout.activity_inicio);
        // Inicializar base de datos y cargar fuentes
        BBDDInitializer.initialize(this);
        // Cargar orden de preferencia
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        preferences.getBoolean("ordenar_fuentes", false);

        // Configurar botones Login y Register
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        buttonRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
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
            case R.id.action_backspace:
                finish();
                break;
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
        menu.findItem(R.id.action_backspace).setVisible(false);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean logged = UserManager.isLoggedIn(this);
        int visibility = logged ? View.GONE : View.VISIBLE;
        buttonLogin.setVisibility(visibility);
        buttonRegister.setVisibility(visibility);
    }

}
