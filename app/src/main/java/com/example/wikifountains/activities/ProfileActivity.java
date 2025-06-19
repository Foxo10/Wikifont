package com.example.wikifountains.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
    private static final int REQUEST_CAMERA = 2001;

    private CircleImageView imageView;
    private TextView textName;
    private TextView textEmail;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap photo = (Bitmap) bundle.get("data");
                    if (photo != null) {
                        imageView.setImageBitmap(photo);
                        uploadPhoto(photo);
                    }
                }
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        try {
                            imageView.setImageURI(uri);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            uploadPhoto(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }
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
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    private void showPhotoDialog() {
        String[] options = {getString(R.string.take_photo), getString(R.string.choose_gallery)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.change_photo)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        takePhoto();
                    } else {
                        pickFromGallery();
                    }
                })
                .show();
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