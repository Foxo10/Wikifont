package com.example.wikifountains.widgets;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.activities.DetallesFuenteActivity;
import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;

import java.util.List;
import java.util.Locale;


public class NearestFuenteWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String name = context.getString(R.string.no_fountains);
        String distanceText = "";
        Fuente nearest = null;

        Location last = getBestLocation(context);
        if (last != null) {
            List<Fuente> fuentes = AppDatabase.getInstance(context).fuenteDao().getAllFuentes();
            float minDist = Float.MAX_VALUE;
            for (Fuente f : fuentes) {
                float[] res = new float[1];
                Location.distanceBetween(last.getLatitude(), last.getLongitude(), f.getLatitud(), f.getLongitud(), res);
                if (res[0] < minDist) {
                    minDist = res[0];
                    nearest = f;
                }
            }
            if (nearest != null) {
                name = nearest.getNombre();
                distanceText = context.getString(R.string.distance) + ": " +
                        String.format(Locale.getDefault(), context.getString(R.string.widget_distance_format), minDist);
            }
        } else {
            name = context.getString(R.string.no_location);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nearest_fuente_widget);
        views.setTextViewText(R.id.textWidgetName, name);
        views.setTextViewText(R.id.textWidgetDistance, distanceText);

        if (nearest != null) {
            Intent intent = new Intent(context, DetallesFuenteActivity.class);
            intent.putExtra("fuente", nearest);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /** Obtiene la ubicación más precisa disponible de los proveedores habilitados. */
    private static Location getBestLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location best = null;
        for (String provider : lm.getProviders(true)) {
            Location l = lm.getLastKnownLocation(provider);
            if (l != null && (best == null || l.getAccuracy() < best.getAccuracy())) {
                best = l;
            }
        }
        return best;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}