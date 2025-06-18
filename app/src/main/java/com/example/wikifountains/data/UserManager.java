package com.example.wikifountains.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase auxiliar para almacenar y recuperar datos de usuario registrados en SharedPreferences.
 */
public class UserManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    public static void saveUser(Context context, String email, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_NAME, name).putString(KEY_EMAIL, email).apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_EMAIL);
    }

    public static String getName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NAME, "");
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, "");
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
