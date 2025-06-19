package com.example.wikifountains.activities;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.data.BBDDInitializer;
import com.example.wikifountains.data.UserManager;
import com.example.wikifountains.receivers.HourlyFuenteReceiver;
import com.example.wikifountains.service.LocationForegroundService;


public class InicioActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button buttonLogin;
    private Button buttonRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentViewWithDrawer(R.layout.activity_inicio);
        // Inicializar base de datos y cargar fuentes
        BBDDInitializer.initialize(this);

        //maybeStartLocationService();

        // Cargar orden de preferencia
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        preferences.getBoolean("ordenar_fuentes", false);

        programarAlarma();

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

    private void programarAlarma() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, HourlyFuenteReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        // TRIGGER A LOS 10 SEGUNDOS
        long triggerAt = System.currentTimeMillis() + 10 * 1000L;

        // REPETICIÓN CADA 30 SEGUNDOS
        long repeatInterval = 30 * 1000L;
        //long triggerAt = System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR;
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                repeatInterval,
                pendingIntent
        );
    }
    private void maybeStartLocationService() {
        boolean locationGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean notifGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifGranted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        if (locationGranted && notifGranted) {
            startLocationService();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startLocationService();
            }
        }
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
