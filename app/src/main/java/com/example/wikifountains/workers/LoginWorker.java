package com.example.wikifountains.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.wikifountains.api.UserApi;

import org.json.JSONObject;


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
            String fetchedName = "";
            String email = "";
            String photo = "";
            if (success) {
                JSONObject user = res.optJSONObject("user");
                if (user != null) {
                    fetchedName = user.optString("name", "");
                    email = user.optString("email", "");
                }
                if (!fetchedName.isEmpty() && !email.isEmpty()) {
                    JSONObject resPhoto = UserApi.getPhoto(fetchedName, email);
                    photo = resPhoto.optString("photo", "");
                }
            }
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

