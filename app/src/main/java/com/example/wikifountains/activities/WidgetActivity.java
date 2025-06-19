package com.example.wikifountains.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WidgetActivity extends BaseActivity {
    private TextView desc;
    private TextView opt1;
    private TextView opt2;
    private TextView opt3;

    private final Handler handler = new Handler();
    private int correctIndex;

    private final Runnable highlightTask = new Runnable() {
        @Override
        public void run() {
            highlight();
            handler.postDelayed(loadTask, 40_000);
        }
    };

    private final Runnable loadTask = new Runnable() {
        @Override
        public void run() {
            loadQuestion();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_widget);
        desc = findViewById(R.id.textWidgetDescription);
        opt1 = findViewById(R.id.textWidgetOption1);
        opt2 = findViewById(R.id.textWidgetOption2);
        opt3 = findViewById(R.id.textWidgetOption3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void loadQuestion() {
        handler.removeCallbacksAndMessages(null);
        List<Fuente> fuentes = AppDatabase.getInstance(this).fuenteDao().getAllFuentes();
        if (fuentes.size() < 3) {
            desc.setText(R.string.quiz_not_enough);
            opt1.setText("");
            opt2.setText("");
            opt3.setText("");
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
        correctIndex = options.indexOf(correct);

        desc.setText(correct.getDescripcion());
        opt1.setText(options.get(0).getNombre());
        opt2.setText(options.get(1).getNombre());
        opt3.setText(options.get(2).getNombre());
        handler.postDelayed(highlightTask, 20_000);
    }

    private void highlight() {
        TextView[] opts = {opt1, opt2, opt3};
        for (int i = 0; i < opts.length; i++) {
            if (i == correctIndex) {
                opts[i].setText(Html.fromHtml("<b>" + opts[i].getText() + "</b>"));
            }
        }
    }
}