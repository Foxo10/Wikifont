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
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHOTO = "photo";

    public UploadPhotoWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString(KEY_NAME);
        String email = getInputData().getString(KEY_EMAIL);
        String photo = getInputData().getString(KEY_PHOTO);
        try {
            JSONObject res = UserApi.uploadPhoto(name, email, photo);
            boolean success = res.optBoolean("success");
            String path = "";
            if (success) {
                path = res.optString("photo_path");
                if (path == null || path.isEmpty()) {
                    path = res.optString("photo", "");
                }
            }
            Data output = new Data.Builder()
                    .putBoolean("success", success)
                    .putString("photo", path)
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
