package com.example.wikifountains.activities;

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
import com.example.wikifountains.data.UserManager;

import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Pantalla de inicio de sesión simple que se autentica mediante el PHP del servidor.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLoginConfirm);

        buttonLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.toast_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                JSONObject res = UserApi.login(email, password);
                if (res.optBoolean("success")) {
                    String name = res.optString("name", "");
                    UserManager.saveUser(this, name, email);
                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}