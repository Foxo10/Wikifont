package com.example.wikifountains.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Actividad que muestra ubicaciones de fuentes en un OpenStreetMap y permite enrutamiento
 * para ellos. Utiliza las bibliotecas OSMDroid y Osmbonuspack.
 */
public class GoogleFontsActivity extends BaseActivity implements MapEventsReceiver {
    private static final int PERMISSION_REQUEST_LOCATION = 1001;

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private CompassOverlay compassOverlay;
    private Polyline roadOverlay;
    private LocationManager locationManager;
    private TextView textRouteInfo;
    private org.osmdroid.util.GeoPoint lastLocationPoint;
    private double totalDistance = 0;
    private GeoPoint currentDestination;
    private Road currentRoad;
    private final List<Marker> nodeMarkers = new ArrayList<>();
    private Marker infoMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargar configuración para OSMDroid
        Configuration.getInstance().load(this,
                PreferenceManager.getDefaultSharedPreferences(this));

        setContentViewWithDrawer(R.layout.activity_google_fonts);

        textRouteInfo = findViewById(R.id.textRouteInfo);

        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(13.0);

        // Vista inicial sobre Uribe Kosta
        BoundingBox uribeKosta = new BoundingBox(43.431272001150184, -2.816241288138726, 43.31081155453468, -3.052685665224482);
        mapView.zoomToBoundingBox(uribeKosta, true);

        // Habilitar gestos de rotación
        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlayManager().add(rotationGestureOverlay);

        // Agregar la superposición de eventos de mapas para manejar prensa larga para eliminar rutas
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(this);
        mapView.getOverlayManager().add(eventsOverlay);

        setupLocationOverlay();
        setupLocationTracking();
        setupCompassOverlay();
        loadFountainMarkers();
    }

    /**
     * Inicializa la superposición de ubicación, solicitando permisos si es necesario.
     */
    private void setupLocationOverlay() {
        lastLocationPoint = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
            return;
        }

        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        locationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            GeoPoint myLoc = locationOverlay.getMyLocation();
            if (myLoc != null) {
                IMapController controller = mapView.getController();
                controller.animateTo(myLoc, 17.0, 1000L);
            }
        }));
        mapView.getOverlays().add(locationOverlay);
    }
    /** Inicia el seguimiento de ubicación para calcular la distancia recorrida. */
    private void setupLocationTracking() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null) return;

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                if (lastLocationPoint != null) {
                    float[] res = new float[1];
                    Location.distanceBetween(
                            lastLocationPoint.getLatitude(), lastLocationPoint.getLongitude(),
                            point.getLatitude(), point.getLongitude(), res);
                    totalDistance += res[0];
                }
                lastLocationPoint = point;
                updateRouteInfoDisplay();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 1, listener, Looper.getMainLooper());
        }
    }

    /** Agrega una superposición de brújula en la parte superior del mapa. */
    private void setupCompassOverlay() {
        compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    /**
     * Carga markers de fuente de la base de datos y los agrega al mapa.
     */
    private void loadFountainMarkers() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Fuente> fuentes = AppDatabase.getInstance(this).fuenteDao().getAllFuentes();
            ArrayList<OverlayItem> items = new ArrayList<>();
            Drawable fountainIcon = ContextCompat.getDrawable(this, R.drawable.ic_fonts);
            for (Fuente f : fuentes) {
                OverlayItem item = new OverlayItem(f.getNombre(), f.getCalle(),
                        new GeoPoint(f.getLatitud(), f.getLongitud()));
                if (fountainIcon != null) item.setMarker(fountainIcon);
                items.add(item);
            }
            ItemizedIconOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(this, items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(int index, OverlayItem item) {
                            drawRouteTo((GeoPoint) item.getPoint());
                            return true;
                        }

                        @Override
                        public boolean onItemLongPress(int index, OverlayItem item) {
                            showFuenteInfo(item);
                            return true;
                        }
                    });
            runOnUiThread(() -> {
                mapView.getOverlays().add(overlay);
                mapView.invalidate();
            });
        });
    }

    /**
     * Dibuja una ruta de caminata desde la ubicación actual hasta el destino.
     */
    private void drawRouteTo(GeoPoint destination) {
        if (locationOverlay == null || locationOverlay.getMyLocation() == null) {
            Toast.makeText(this, R.string.no_location, Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                RoadManager roadManager = new OSRMRoadManager(this, "wikifont-user");
                ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);
                ArrayList<GeoPoint> waypoints = new ArrayList<>();
                waypoints.add(locationOverlay.getMyLocation());
                waypoints.add(destination);
                Road road = roadManager.getRoad(waypoints);
                Polyline polyline = RoadManager.buildRoadOverlay(road);
                Paint paint = polyline.getPaint();
                paint.setColor(getColor(R.color.amber));
                paint.setStrokeWidth(8f);

                runOnUiThread(() -> {
                    removeRoute();
                    roadOverlay = polyline;
                    currentRoad = road;
                    currentDestination = destination;
                    mapView.getOverlays().add(roadOverlay);
                    Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
                    for (int i = 0; i < road.mNodes.size(); i++) {
                        RoadNode node = road.mNodes.get(i);
                        Marker nodeMarker = new Marker(mapView);
                        nodeMarker.setPosition(node.mLocation);
                        nodeMarker.setIcon(nodeIcon);
                        nodeMarker.setTitle(getString(R.string.step) + " " + i);
                        nodeMarkers.add(nodeMarker);
                        mapView.getOverlays().add(nodeMarker);
                    }
                    mapView.invalidate();
                    updateRouteInfoDisplay();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.route_error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /** Elimina la superposición de ruta actual del mapa si existe alguna. */
    private void removeRoute() {
        if (roadOverlay != null) {
            mapView.getOverlays().remove(roadOverlay);
            roadOverlay = null;
            currentRoad = null;
            currentDestination = null;
            for (Marker m : nodeMarkers) {
                mapView.getOverlays().remove(m);
            }
            nodeMarkers.clear();
            currentRoad = null;
            currentDestination = null;
            if (infoMarker != null) {
                mapView.getOverlays().remove(infoMarker);
                infoMarker = null;
            }
            mapView.invalidate();
            updateRouteInfoDisplay();

        }
    }
    private void showFuenteInfo(OverlayItem item) {
        if (infoMarker != null) {
            mapView.getOverlays().remove(infoMarker);
        }
        infoMarker = new Marker(mapView);
        infoMarker.setPosition((GeoPoint) item.getPoint());
        infoMarker.setTitle(item.getTitle());
        infoMarker.setSnippet(item.getSnippet());
        Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_fonts);
        if (icon != null) {
            infoMarker.setIcon(icon);
        }
        infoMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(infoMarker);
        infoMarker.showInfoWindow();
        mapView.invalidate();
    }
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    /**
     * Pulsación larga en cualquier lugar del mapa elimina la ruta dibujada.
     */
    @Override
    public boolean longPressHelper(GeoPoint p) {
        removeRoute();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupLocationOverlay();
            setupLocationTracking();
        }
    }
    /** Actualiza el texto de distancia y tiempo mostrado al usuario. */
    private void updateRouteInfoDisplay() {
        if (textRouteInfo == null) return;

        StringBuilder sb = new StringBuilder();
        if (currentRoad != null) {
            sb.append(getString(R.string.distance)).append(": ")
                    .append(String.format("%.1f km", currentRoad.mLength));
            sb.append('\n');
            sb.append(getString(R.string.time)).append(": ")
                    .append(String.format("%.1f min", currentRoad.mDuration / 60));
            sb.append('\n');
        }
        sb.append(getString(R.string.total_distance)).append(": ")
                .append(String.format("%.1f km", totalDistance / 1000));

        textRouteInfo.setText(sb.toString());
    }
}