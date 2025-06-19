package com.example.wikifountains.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.wikifountains.R;
import com.example.wikifountains.api.UserApi;
import com.example.wikifountains.data.UserManager;
import com.example.wikifountains.workers.UploadPhotoWorker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Pantalla de perfil de usuario que mustra la información del usuario y permite actualizar la foto de perfil.
 */
public class ProfileActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION_CAMERA = 2001;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2003;
    private CircleImageView imageView;
    private TextView textName;
    private TextView textEmail;
    private Uri photoURI;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        // Procesar y guardar la imagen
                        if (bitmap != null) {
                            uploadPhoto(bitmap);
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(
                                    ProfileActivity.this,
                                    getString(R.string.error_loading_image),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(
                                ProfileActivity.this,
                                getString(R.string.error_loading_image),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            });
    private final ActivityResultLauncher<Intent> pickMedia =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            uploadPhoto(bitmap);
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, R.string.error_loading_image, Toast.LENGTH_SHORT).show();
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
        String storedPhoto = UserManager.getPhoto(this);
        if (!storedPhoto.isEmpty()) {
            Glide.with(this)
                    .load(UserApi.BASE_URL + storedPhoto)
                    .placeholder(R.drawable.ic_account)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_account);
        }

        buttonChange.setOnClickListener(v -> showPhotoDialog());
    }


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void uploadPhoto(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fototransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
        Data input = new Data.Builder()
                .putString(UploadPhotoWorker.KEY_NAME, UserManager.getName(this))
                .putString(UploadPhotoWorker.KEY_EMAIL, UserManager.getEmail(this))
                .putString(UploadPhotoWorker.KEY_PHOTO, fotoen64)
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadPhotoWorker.class)
                .setInputData(input)
                .setConstraints(constraints)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        wm.getWorkInfoByIdLiveData(request.getId()).observe(this, info -> {
            if (info != null && info.getState().isFinished()) {
                if (info.getState() == WorkInfo.State.SUCCEEDED) {
                    String photo = info.getOutputData().getString("photo");
                    UserManager.saveUser(this, UserManager.getName(this),
                            UserManager.getEmail(this), photo);
                    Glide.with(this)
                            .load(UserApi.BASE_URL + photo)
                            .placeholder(R.drawable.ic_account)
                            .into(imageView);
                    updateNavHeader();
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickMedia.launch(intent);
    }

    private void showPhotoDialog() {
        String[] options = {getString(R.string.take_photo), getString(R.string.choose_gallery)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.change_photo)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpen();
                        takePhoto();
                    } else {
                        checkGalleryPermissionAndOpen();
                        pickFromGallery();
                    }
                })
                .show();
    }

    private void checkGalleryPermissionAndOpen() {
        // Este es el código de verificación de permisos de galería
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: Necesitamos READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                // Verifica si debemos mostrar una explicación
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_MEDIA_IMAGES)) {
                    // Muestra una explicación al usuario
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.permission_needed))
                            .setMessage(getString(R.string.permission_gallery_explanation))
                            .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                                // Solicita el permiso después de que el usuario vea la explicación
                                ActivityCompat.requestPermissions(ProfileActivity.this,
                                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                            })
                            .setNegativeButton(getString(R.string.cancel), null)
                            .create()
                            .show();
                } else {
                    // No necesita explicación, solicita directamente
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                }
            } else {
                pickFromGallery();
            }
        } else {
            // Android 12 y versiones anteriores: Usamos READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Verifica si debemos mostrar una explicación
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Muestra una explicación al usuario
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.permission_needed))
                            .setMessage(getString(R.string.permission_gallery_explanation))
                            .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                                // Solicita el permiso después de que el usuario vea la explicación
                                ActivityCompat.requestPermissions(ProfileActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                            })
                            .setNegativeButton(getString(R.string.cancel), null)
                            .create()
                            .show();
                } else {
                    // No necesita explicación, solicita directamente
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                }
            } else {
                pickFromGallery();
            }
        }
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Mostrar explicación
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_needed))
                        .setMessage(getString(R.string.permission_camera_explanation))
                        .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                            ActivityCompat.requestPermissions(ProfileActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_PERMISSION_CAMERA);
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .create()
                        .show();
            } else {
                // Solicitar permiso directamente
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);
            }
        } else {
            takePhoto();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        }
    }
}