package com.example.wikifountains.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.wikifountains.api.UserApi;

import org.json.JSONObject;

/**
 * Worker que sube una foto al servidor
 */
public class UploadPhotoWorker extends Worker {
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHOTO = "photo";

    public UploadPhotoWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String email = getInputData().getString(KEY_EMAIL);
        String photo = getInputData().getString(KEY_PHOTO);
        try {
            JSONObject res = UserApi.updatePhoto(email, photo);
            boolean success = res.optBoolean("success");
            Data output = new Data.Builder()
                    .putBoolean("success", success)
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
