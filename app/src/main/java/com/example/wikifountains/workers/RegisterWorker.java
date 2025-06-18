package com.example.wikifountains.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.wikifountains.api.UserApi;

import org.json.JSONObject;


/**
 * Worker que realiza el registro de usuarios en segundo plano mediante WorkManager.
 */
public class RegisterWorker extends Worker {
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    public RegisterWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString(KEY_NAME);
        String email = getInputData().getString(KEY_EMAIL);
        String password = getInputData().getString(KEY_PASSWORD);
        try {
            JSONObject res = UserApi.register(name, email, password);
            boolean success = res.optBoolean("success");
            Data output = new Data.Builder()
                    .putBoolean("success", success)
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
