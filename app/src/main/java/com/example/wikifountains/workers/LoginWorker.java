package com.example.wikifountains.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.wikifountains.api.UserApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Worker que realiza el login de usuarios en segundo plano mediante WorkManager.
 */
public class LoginWorker extends Worker {
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";

    public LoginWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString(KEY_NAME);
        String password = getInputData().getString(KEY_PASSWORD);
        try {
            JSONObject res = UserApi.login(name, password);
            boolean success = res.optBoolean("success");
            Data output = new Data.Builder()
                    .putBoolean("success", success)
                    .putString("name", res.optString("name", ""))
                    .putString("email", res.optString("email", ""))
                    .putString("photo", res.optString("photo", ""))
                    .build();
            if (success) {
                return Result.success(output);
            } else {
                return Result.failure(output);
            }
        } catch (Exception e) {
            return Result.failure();
        }
    }
}

