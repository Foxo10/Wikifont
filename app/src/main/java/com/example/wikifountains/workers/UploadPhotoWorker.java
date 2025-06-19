package com.example.wikifountains.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.wikifountains.api.UserApi;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Worker que sube una foto al servidor
 */
public class UploadPhotoWorker extends Worker {
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHOTO_PATH = "photo_path";

    public UploadPhotoWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString(KEY_NAME);
        String email = getInputData().getString(KEY_EMAIL);
        String photoPath = getInputData().getString(KEY_PHOTO_PATH);
        try {
            File imageFile = new File(photoPath);
            if (!imageFile.exists()) {
                Log.e("UploadPhotoWorker", "File does not exist: " + photoPath);
                return Result.failure();
            }

            String base64Image = encodeImageToBase64(imageFile);

            JSONObject res = UserApi.uploadPhoto(name, email, base64Image);

            Log.d("UploadPhotoWorker", "Respuesta del servidor: " + res.toString());

            boolean success = res.optBoolean("success", false);
            String path = res.optString("photo_path", "");

            Data output = new Data.Builder()
                    .putBoolean("success", success)
                    .putString("photo", path)
                    .build();

            if (success) {
                return Result.success(output);
            } else {
                Log.e("UploadPhotoWorker", "Upload failed. Response: " + res.toString());
                return Result.failure(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UploadPhotoWorker", "Exception: " + e.getMessage());
            return Result.failure();
        }
    }
    private String encodeImageToBase64(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
