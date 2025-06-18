package com.example.wikifountains.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.wikifountains.R;
import com.example.wikifountains.api.UserApi;
import com.example.wikifountains.data.UserManager;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Pantalla de perfil de usuario que mustra la información del usuario y permite actualizar la foto de perfil.
 */
public class ProfileActivity extends BaseActivity {
    private static final int REQUEST_CAMERA = 2001;

    private CircleImageView imageView;
    private TextView textName;
    private TextView textEmail;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    if (photo != null) {
                        imageView.setImageBitmap(photo);
                        uploadPhoto(photo);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDrawer(R.layout.activity_profile);

        imageView = findViewById(R.id.imageViewProfile);
        textName = findViewById(R.id.textViewProfileName);
        textEmail = findViewById(R.id.textViewProfileEmail);
        Button buttonChange = findViewById(R.id.buttonChangePhoto);

        textName.setText(UserManager.getName(this));
        textEmail.setText(UserManager.getEmail(this));
        Glide.with(this)
                .load(UserManager.getPhoto(this))
                .placeholder(R.drawable.ic_account)
                .into(imageView);

        buttonChange.setOnClickListener(v -> takePhoto());
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void uploadPhoto(Bitmap bitmap) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String encoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                String email = UserManager.getEmail(this);
                var res = UserApi.updatePhoto(email, encoded);
                if (res.optBoolean("success")) {
                    String photo = res.optString("photo", "");
                    UserManager.saveUser(this, UserManager.getName(this), email, photo);
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        }
    }
}