package com.example.wikifountains.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class LocationForegroundService extends Service {
    private static final String CHANNEL_ID = "location_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int RADIUS_METERS = 200;

    private FusedLocationProviderClient fusedLocationClient;
    private NotificationManager notificationManager;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_LOW.equals(action)) {
                stopLocationUpdates();
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                // Placeholder for connectivity related actions
            }
        }
    };

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            if (result == null) return;
            Location location = result.getLastLocation();
            if (location != null) {
                int count = countNearbyFountains(location.getLatitude(), location.getLongitude());
                updateNotification(count);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        registerReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }
        createNotificationChannel();
        Notification notification = buildNotification(0);
        startForeground(NOTIFICATION_ID, notification);
        requestLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        unregisterReceiver(broadcastReceiver);
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        LocationRequest request = LocationRequest.create();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private int countNearbyFountains(double lat, double lon) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        List<Fuente> fuentes = db.fuenteDao().getAllFuentes();
        int count = 0;
        float[] result = new float[1];
        for (Fuente f : fuentes) {
            Location.distanceBetween(lat, lon, f.getLatitud(), f.getLongitud(), result);
            if (result[0] <= RADIUS_METERS) {
                count++;
            }
        }
        return count;
    }

    private void updateNotification(int count) {
        Notification notification = buildNotification(count);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification(int count) {
        String text = getString(R.string.nearby_fountains, count, RADIUS_METERS);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_noti_fountain)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.location_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }
}