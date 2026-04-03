package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    private Button btnSendResetEmail;
    private EditText editEmailForMapping;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btnSendResetEmail = findViewById(R.id.btnVerifyViaEmail);
        editEmailForMapping = findViewById(R.id.editEmailForMap);
        btnBack = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnSendResetEmail.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        String email = editEmailForMapping.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();

                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        String msg = "Error sending reset email";
                        if (task.getException() != null &&
                                task.getException().getMessage() != null) {
                            msg = task.getException().getMessage();
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}