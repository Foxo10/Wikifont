package com.example.wikifountains.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;

import java.util.List;
import java.util.Locale;

public class WidgetActivity extends BaseActivity {
    private TextView nameView;
    private TextView distanceView;
    private final Handler handler = new Handler();

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateViews();
            handler.postDelayed(this, 30 * 60 * 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_widget);
        nameView = findViewById(R.id.textWidgetName);
        distanceView = findViewById(R.id.textWidgetDistance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updater.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updater);
    }

    private void updateViews() {
        String name = getString(R.string.no_fountains);
        String distanceText = "";
        Fuente nearest = null;

        Location last = getBestLocation();
        if (last != null) {
            List<Fuente> fuentes = AppDatabase.getInstance(this).fuenteDao().getAllFuentes();
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
                distanceText = getString(R.string.distance) + ": " +
                        String.format(Locale.getDefault(), getString(R.string.widget_distance_format), minDist);
            }
        } else {
            name = getString(R.string.no_location);
        }

        nameView.setText(name);
        distanceView.setText(distanceText);
    }

    /** Obtiene la mejor ubicación conocida de cualquier proveedor habilitado. */
    private Location getBestLocation() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm == null || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}