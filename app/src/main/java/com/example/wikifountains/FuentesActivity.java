package com.example.wikifountains;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class FuentesActivity extends AppCompatActivity implements
        EliminarFuenteDialog.EliminarFuenteListener,
        FuenteAdapter.OnGuardarNotificacionClickListener {

    private RecyclerView recyclerView;
    private FuenteAdapter adapter;
    private AppDatabase db;
    private String localidadSeleccionado;
    private Button buttonAddFuente;
    private TextView textoFuentesDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearCanalNotificaciones(); // Crear el canal de notificaciones

        // Recibir el pueblo seleccionado desde InicioActivity
        Intent intent = getIntent();
        localidadSeleccionado = intent.getStringExtra("localidad");
        textoFuentesDe = findViewById(R.id.textViewFuentesDe);

        // Obtener el texto formateado según el idioma
        String textoFormateado = getString(R.string.fuentes_de, localidadSeleccionado);
        textoFuentesDe.setText(textoFormateado);

        // Inicializar la base de datos
        db = AppDatabase.getInstance(this);

        // Cargar datos iniciales desde el CSV si es necesario
        cargarDatosIniciales();

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFuentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar y configurar botón de añadir fuente
        buttonAddFuente = findViewById(R.id.buttonAddFuente);
        buttonAddFuente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FuentesActivity.this, AddFuenteActivity.class);
                intent.putExtra("localidadseleccionado", localidadSeleccionado);
                startActivity(intent);
            }
        });

        // Cargar las fuentes desde la base de datos
        cargarFuentes();
    }

    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_fuentes", // ID del canal
                    "Notificaciones de fuentes", // Nombre del canal
                    NotificationManager.IMPORTANCE_DEFAULT // Importancia del canal
            );
            channel.setDescription("Notificaciones para acciones relacionadas con fuentes");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFuentes();
    }

    private void cargarFuentes() {
        // Obtener las fuentes filtradas por pueblo
        Log.d("tag 4", "Cargando fuentes para la localidad: " + localidadSeleccionado);
        List<Fuente> fuentes = db.fuenteDao().getFuentesPorLocalidad(localidadSeleccionado);
        Log.d("tag 5", "Número de fuentes encontradas: " + (fuentes != null ? fuentes.size() : "null"));

        // Asignar las fuentes al adaptador
        adapter = new FuenteAdapter(fuentes);
        adapter.setEliminarFuenteListener(this);
        adapter.setOnGuardarNotificacionClickListener(this); // Asignar el listener para "guardar como notificación"
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEliminarConfirmado(Fuente fuente) {
        // Eliminar la fuente de la base de datos
        Executors.newSingleThreadExecutor().execute(() -> {
            db.fuenteDao().deleteFuente(fuente);
            runOnUiThread(() -> {
                cargarFuentes(); // Recargar las fuentes después de eliminar
            });
        });
    }

    @Override
    public void onGuardarNotificacionClick(Fuente fuente) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 11);
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "canal_fuentes";

        // Crear un Intent para abrir una actividad con los detalles de la fuente
        Intent intent = new Intent(this, DetallesFuenteActivity.class);
        intent.putExtra("fuente", fuente); // Pasar la fuente a la actividad
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación persistente
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_noti_fountain) // Icono de la notificación
                .setContentTitle("Fuente "+fuente.getNombre()+" guardada") // Título de la notificación
                .setContentText("Has guardado la fuente: " + fuente.getNombre()) // Mensaje de la notificación
                .setSubText(fuente.getCalle()) // Subtexto de la notificación
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad de la notificación
                .setContentIntent(pendingIntent) // Intent al hacer clic en la notificación
                .setAutoCancel(true) // La notificación se cierra al hacer clic en ella
                .setOngoing(true); // Hace que la notificación sea persistente (no se puede descartar)

        // Mostrar la notificación
        notificationManager.notify(fuente.getId(), builder.build()); // Usar el ID de la fuente como ID de notificación
    }

    private List<Fuente> cargarFuentesDesdeCSV() {
        List<Fuente> fuentes = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(R.raw.fuentes);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(inputStreamReader);

        try {
            String[] nextLine;
            boolean isFirstLine = true; // Para saltar la primera línea (cabeceras)
            while ((nextLine = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Saltar la primera línea (cabeceras)
                }

                // Crear un objeto Fuente con los datos de la línea
                String nombre = nextLine[0];
                String localidad = nextLine[1];
                String calle = nextLine[2];

                Fuente fuente = new Fuente(nombre, localidad, calle,"","");
                fuentes.add(fuente);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        } finally {
            try {
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fuentes;
    }

    private void cargarDatosIniciales() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Verificar si ya hay fuentes en la base de datos
            if (db.fuenteDao().countFuentes() <= 10) {
                // Cargar fuentes desde el CSV
                List<Fuente> fuentes = cargarFuentesDesdeCSV();

                // Insertar las fuentes en la base de datos
                for (Fuente fuente : fuentes) {
                    db.fuenteDao().insert(fuente);
                }

                Log.d("Database", "Datos iniciales cargados en la base de datos.");
            }
        });
    }
}