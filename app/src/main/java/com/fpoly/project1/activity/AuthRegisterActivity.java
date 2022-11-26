package com.fpoly.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fpoly.project1.R;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText inputEmail = findViewById(R.id.register_edt_email);
        EditText inputPassword = findViewById(R.id.register_edt_password);
        EditText inputPasswordConfirm = findViewById(R.id.register_edt_confirm_password);

        findViewById(R.id.register_txt_signIn).setOnClickListener(v -> {
            startActivity(new Intent(AuthRegisterActivity.this, AuthLoginActivity.class));
            finish();
        });
        findViewById(R.id.register_btn_signUp)
                .setOnClickListener(v -> {
                    boolean hasError = false;

                    if (inputEmail.getText().length() == 0) {
                        hasError = true;
                        inputEmail.setError("Field cannot be empty");
                    }

                    if (inputPassword.getText().length() == 0) {
                        hasError = true;
                        inputPassword.setError("Field cannot be empty");
                    }

                    if (inputPasswordConfirm.getText().length() == 0) {
                        hasError = true;
                        inputPasswordConfirm.setError("Field cannot be empty");
                    }

                    if (!inputPassword.getText().toString().equals(inputPasswordConfirm.getText().toString())) {
                        hasError = true;
                        inputPassword.setError("Password mismatch");
                        inputPasswordConfirm.setError("Password mismatch");
                    }

                    if (!hasError) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                inputEmail.getText().toString(),
                                inputPassword.getText().toString()
                        ).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(AuthRegisterActivity.this, AuthFillBioActivity.class));
                                finish();
                            } else {
                                Toast.makeText(AuthRegisterActivity.this, "Failed to register. Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}
