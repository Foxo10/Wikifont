package com.example.wikifountains.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wikifountains.R;
import com.example.wikifountains.api.UserApi;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String PREFS_NAME = "MyPrefs";
    protected static final String LANGUAGE_KEY = "language";
    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;
    public static final int THEME_FONT = 3;

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;
    protected NavigationView navigationView;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    protected void loadLocale() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = preferences.getString(LANGUAGE_KEY, "es");
        if (!language.isEmpty()) {
            setLocale(language);
        }
    }

    protected void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    protected void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", THEME_SYSTEM);

        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_WikiFountains_Light);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                setTheme(R.style.Theme_WikiFountains);
                break;
            case THEME_FONT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_WikiFountains_Font);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_WikiFountains);
                break;
        }
    }
    protected void setContentViewWithDrawer(@LayoutRes int layoutResId) {
        super.setContentView(R.layout.base_activity);

        LayoutInflater.from(this).inflate(layoutResId, findViewById(R.id.content_frame), true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        updateNavHeader();

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this::onDrawerItemSelected);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inicio_menu, menu);
        if (this instanceof InicioActivity) {
            menu.findItem(R.id.action_backspace).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        if (this instanceof InicioActivity && id == R.id.action_home) {
            return true;
        }
        switch (id) {
            case R.id.action_home:
                intent = new Intent(this, InicioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.action_backspace:
                finish();
                break;
            case R.id.action_search:
                intent = new Intent(this, PueblosActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_google_maps:
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=fuentes");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Actualiza el drawer header con la info del usuario actual. */
    protected void updateNavHeader() {
        if (navigationView == null) return;
        android.view.View header = navigationView.getHeaderView(0);
        android.widget.TextView name = header.findViewById(R.id.textViewNavHeaderName);
        android.widget.TextView email = header.findViewById(R.id.textViewNavHeaderEmail);
        de.hdodenhof.circleimageview.CircleImageView avatar = header.findViewById(R.id.imageViewNavHeaderProfile);
        header.setOnClickListener(v -> {
            if (com.example.wikifountains.data.UserManager.isLoggedIn(this)) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        if (com.example.wikifountains.data.UserManager.isLoggedIn(this)) {
            name.setText(com.example.wikifountains.data.UserManager.getName(this));
            email.setText(com.example.wikifountains.data.UserManager.getEmail(this));
            String photo = com.example.wikifountains.data.UserManager.getPhoto(this);
            if (!photo.isEmpty()) {
                Glide.with(this)
                        .load(UserApi.BASE_URL + photo)
                        .placeholder(R.drawable.ic_account)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(avatar);
            } else {
                avatar.setImageResource(R.drawable.ic_account);
            }
        } else {
            name.setText(getString(R.string.app_name));
            email.setText(getString(R.string.access));
            avatar.setImageResource(R.drawable.ic_account);
        }
    }
    private boolean onDrawerItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ajustes:
                startActivityForResult(new Intent(this, OptionsActivity.class), 1);
                break;
            case R.id.localizacion:
                startActivity(new Intent(this, GoogleFontsActivity.class));
                break;
            case R.id.widget:
                startActivity(new Intent(this, WidgetActivity.class));
                break;
            case R.id.logout:
                com.example.wikifountains.data.UserManager.clear(this);
                updateNavHeader();
                startActivityForResult(new Intent(this, InicioActivity.class), 1);
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
    }
}

