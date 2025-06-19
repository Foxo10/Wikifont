package com.example.wikifountains.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.activities.DetallesFuenteActivity;
import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;

import java.util.List;
import java.util.Random;

public class HourlyFuenteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        AppDatabase db = AppDatabase.getInstance(context);
        List<Fuente> fuentes = db.fuenteDao().getAllFuentes();
        if (fuentes == null || fuentes.isEmpty()) {
            return;
        }

        Fuente fuente = fuentes.get(new Random().nextInt(fuentes.size()));

        Intent detalleIntent = new Intent(context, DetallesFuenteActivity.class);
        detalleIntent.putExtra("fuente", fuente);
        detalleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                detalleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_fuentes")
                .setSmallIcon(R.drawable.ic_noti_fountain)
                .setContentTitle(context.getString(R.string.font) + " " + fuente.getNombre())
                .setContentText(context.getString(R.string.more_info))
                .setSubText(fuente.getCalle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        manager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_fuentes",
                    "Notificaciones de fuentes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones para acciones relacionadas con fuentes");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}