package com.example.wikifountains.widgets;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.text.Html;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.activities.DetallesFuenteActivity;
import com.example.wikifountains.data.AppDatabase;
import com.example.wikifountains.data.Fuente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class FuenteQuizWidget extends AppWidgetProvider {
    private static final String PREF = "quiz_widget";
    private static final String ACTION_HIGHLIGHT = "com.example.wikifountains.HIGHLIGHT";
    private static final String ACTION_NEW_QUESTION = "com.example.wikifountains.NEW_QUESTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (ACTION_HIGHLIGHT.equals(intent.getAction())) {
            highlight(context, mgr, id);
        } else if (ACTION_NEW_QUESTION.equals(intent.getAction())) {
            updateQuestion(context, mgr, id);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateQuestion(context, appWidgetManager, widgetId);
        }
    }

    private static void updateQuestion(Context context, AppWidgetManager manager, int widgetId) {
        List<Fuente> fuentes = AppDatabase.getInstance(context).fuenteDao().getAllFuentes();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nearest_fuente_widget);
        if (fuentes.size() < 3) {
            views.setTextViewText(R.id.textWidgetDescription,
                    context.getString(R.string.quiz_not_enough));
            manager.updateAppWidget(widgetId, views);
            return;
        }
        Random r = new Random();
        Fuente correct = fuentes.get(r.nextInt(fuentes.size()));
        List<Fuente> options = new ArrayList<>();
        options.add(correct);
        while (options.size() < 3) {
            Fuente f = fuentes.get(r.nextInt(fuentes.size()));
            if (!options.contains(f)) options.add(f);
        }
        Collections.shuffle(options, r);
        int correctIndex = options.indexOf(correct);

        SharedPreferences.Editor ed = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();
        ed.putString("desc" + widgetId, correct.getDescripcion());
        ed.putString("opt1" + widgetId, options.get(0).getNombre());
        ed.putString("opt2" + widgetId, options.get(1).getNombre());
        ed.putString("opt3" + widgetId, options.get(2).getNombre());
        ed.putInt("correct" + widgetId, correctIndex);
        ed.apply();

        views.setTextViewText(R.id.textWidgetDescription, correct.getDescripcion());
        views.setTextViewText(R.id.textWidgetOption1, options.get(0).getNombre());
        views.setTextViewText(R.id.textWidgetOption2, options.get(1).getNombre());
        views.setTextViewText(R.id.textWidgetOption3, options.get(2).getNombre());
        manager.updateAppWidget(widgetId, views);

        scheduleIntent(context, ACTION_HIGHLIGHT, widgetId, 20_000);
        scheduleIntent(context, ACTION_NEW_QUESTION, widgetId, 60_000);
    }

    private static void highlight(Context context, AppWidgetManager manager, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String desc = prefs.getString("desc" + widgetId, null);
        if (desc == null) {
            updateQuestion(context, manager, widgetId);
            return;
        }
        String opt1 = prefs.getString("opt1" + widgetId, "");
        String opt2 = prefs.getString("opt2" + widgetId, "");
        String opt3 = prefs.getString("opt3" + widgetId, "");
        int correct = prefs.getInt("correct" + widgetId, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nearest_fuente_widget);
        views.setTextViewText(R.id.textWidgetDescription, desc);
        CharSequence[] opts = {
                opt1,
                opt2,
                opt3
        };
        int[] ids = {R.id.textWidgetOption1, R.id.textWidgetOption2, R.id.textWidgetOption3};
        for (int i = 0; i < ids.length; i++) {
            if (i == correct) {
                views.setTextViewText(ids[i], Html.fromHtml("<b>" + opts[i] + "</b>"));
            } else {
                views.setTextViewText(ids[i], opts[i]);
            }
        }
        manager.updateAppWidget(widgetId, views);
    }

    private static void scheduleIntent(Context context, String action, int widgetId, long delayMs) {
        Intent intent = new Intent(context, FuenteQuizWidget.class);
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pi = PendingIntent.getBroadcast(context, (action.hashCode() ^ widgetId), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, pi);
        }
    }
}