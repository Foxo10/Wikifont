package com.example.wikifountains.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.EliminarFuenteDialog;
import com.example.wikifountains.data.Fuente;
import com.example.wikifountains.R;
import com.example.wikifountains.adapters.FuenteAdapter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FuentesActivity extends AppCompatActivity implements
        EliminarFuenteDialog.EliminarFuenteListener,
        FuenteAdapter.OnGuardarNotificacionClickListener,
        FuenteAdapter.OnMapsClickListener  {

    private RecyclerView recyclerView;
    private FuenteAdapter adapter;
    private AppDatabase db;
    private String localidadSeleccionado;
    private Button buttonAddFuente;
    private TextView textoFuentesDe;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fuentes);

        // Inicializar vistas
        // Inicializar vistas
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewFuentes);
        buttonAddFuente = findViewById(R.id.buttonAddFuente);
        tvEmptyState = findViewById(R.id.tvEmptyState);

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

        // Cargar datos iniciales desde el CSV
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
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        Executors.newSingleThreadExecutor().execute(()-> {
            // Obtener las fuentes filtradas por pueblo
            Log.d("tag 4", "Cargando fuentes para la localidad: " + localidadSeleccionado);
            List<Fuente> fuentes = db.fuenteDao().getFuentesPorLocalidad(localidadSeleccionado);
            Log.d("tag 5", "Número de fuentes encontradas: " + (fuentes != null ? fuentes.size() : "null"));

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE); // Ocultar ProgressBar

                if (fuentes == null || fuentes.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE); // Mostrar mensaje
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new FuenteAdapter(fuentes);
                    // Configurar listeners del adaptador
                    adapter.setEliminarFuenteListener(this);
                    adapter.setOnGuardarNotificacionClickListener(this); // Asignar el listener para "guardar como notificación"
                    adapter.setOnMapsClickListener(this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setAdapter(adapter);
                }
            });
        });

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
        String[] nextLine;

        try {
            boolean isFirstLine = true;
            while ((nextLine = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Corregir manejo de campos vacíos
                String nombre = nextLine.length > 0 ? nextLine[0] : "";
                String localidad = nextLine.length > 1 ? nextLine[1] : "";
                String calle = nextLine.length > 2 ? nextLine[2] : "";

                // Coordenadas: evitar índices fuera de rango
                String latitud = nextLine.length > 3 ? nextLine[3] : "";
                String longitud = nextLine.length > 4 ? nextLine[4] : "";
                String coordenadas = !latitud.isEmpty() && !longitud.isEmpty() ? latitud + "," + longitud : "";

                // Descripción: corregir asignación condicional
                String descripcion = nextLine.length > 5 ? nextLine[5] : "Sin descripción";

                Fuente fuente = new Fuente(nombre, localidad, calle, coordenadas, descripcion);
                fuentes.add(fuente);
            }
        } catch (Exception e) {
            Log.e("CSV Error", "Error al leer CSV", e);
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
        progressBar.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Fuente> fuentes = cargarFuentesDesdeCSV();
            Log.d("DEBUG", "Número de fuentes en CSV: " + fuentes.size());

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                cargarFuentes(); // Recargar después de insertar datos
            });

            for (Fuente fuente : fuentes) {
                // Verificar si la fuente ya existe
                Fuente existente = db.fuenteDao().getFuenteByNombre(fuente.getNombre());
                if (existente == null) {
                    db.fuenteDao().insert(fuente);
                    Log.d("DEBUG", "Insertando fuente: " + fuente.getNombre() + " - Coordenadas: " + fuente.getCoordenadas());
                } else {
                    Log.d("DEBUG", "Ya existe: " + fuente.getNombre());
                }
            }
        });
    }

    @Override
    public void onMapsClick(Fuente fuente) {
        Log.d("MapsClick", "Iniciando clic en Maps para: " + fuente.getNombre());
        Log.d("Validación", "Coordenadas: " + fuente.getCoordenadas());

        if (!fuente.tieneCoordenadasValidas()) {
            Log.e("Error", "Coordenadas inválidas: " + fuente.getCoordenadas());
            Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String[] partes = fuente.getCoordenadas().split(",");
            Log.d("Split", "Latitud: " + partes[0] + " | Longitud: " + partes[1]);

            double latitud = Double.parseDouble(partes[0]);
            double longitud = Double.parseDouble(partes[1]);
            Log.d("Coords", "Lat: " + latitud + " | Lon: " + longitud);

            // Intent para Google Maps
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + latitud + "," + longitud);
            Intent streetViewIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            streetViewIntent.setPackage("com.google.android.apps.maps");

            if (streetViewIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(streetViewIntent);
            } else {
                String urlWeb = "https://www.google.com/maps/@?api=1&map_action=pano&viewpoint=" + latitud + "," + longitud;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb)));
            }

        } catch (Exception e) {
            Log.e("Error", "Excepción en onMapsClick: " + e.getMessage());
            Toast.makeText(this, "Error al abrir Maps", Toast.LENGTH_SHORT).show();
        }
    }

}