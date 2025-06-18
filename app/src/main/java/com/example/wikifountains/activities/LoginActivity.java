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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.wikifountains.R;
import com.example.wikifountains.api.UserApi;
import com.example.wikifountains.data.UserManager;
import com.example.wikifountains.workers.LoginWorker;

import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Pantalla de inicio de sesión simple que se autentica mediante el PHP del servidor.
 */
public class LoginActivity extends BaseActivity {
    private EditText editName;
    private EditText editPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editName = findViewById(R.id.editTextName);
        editPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLoginConfirm);

        buttonLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String name = editName.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.toast_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        Data input = new Data.Builder()
                .putString(LoginWorker.KEY_NAME, name)
                .putString(LoginWorker.KEY_PASSWORD, password)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LoginWorker.class)
                .setInputData(input)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        wm.getWorkInfoByIdLiveData(request.getId()).observe(this, info -> {
            if (info != null && info.getState().isFinished()) {
                if (info.getState() == WorkInfo.State.SUCCEEDED) {
                    String fetchedName = info.getOutputData().getString("name");
                    String email = info.getOutputData().getString("email");
                    String photo = info.getOutputData().getString("photo");
                    UserManager.saveUser(this, fetchedName, email, photo);
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}