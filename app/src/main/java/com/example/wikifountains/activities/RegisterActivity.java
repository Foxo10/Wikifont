package com.example.wikifountains.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wikifountains.R;
import com.example.wikifountains.api.UserApi;

import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Pantalla para crear una nueva cuenta de usuario.
 */
public class RegisterActivity extends BaseActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegisterConfirm);

        buttonRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.toast_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                JSONObject res = UserApi.register(name, email, password);
                if (res.optBoolean("success")) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, R.string.register_error, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}