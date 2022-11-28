package com.fpoly.project1.activity.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthResetPassword extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        EditText inputPassword = findViewById(R.id.resetPassword_edt_newPass);
        EditText inputPasswordConfirm = findViewById(R.id.resetPassword_edt_confirmNewPass);

        findViewById(R.id.resetPassword_btn_save).setOnClickListener(v -> {
            boolean hasError = false;

            if (inputPassword.getText().toString().length() == 0) {
                inputPassword.setError("Field must not be empty");
                hasError = true;
            }
            if (inputPasswordConfirm.getText().toString().length() == 0) {
                inputPasswordConfirm.setError("Field must not be empty");
                hasError = true;
            }
            if (!inputPassword.getText().toString().equals(inputPasswordConfirm.getText().toString())) {
                inputPassword.setError("Password mismatch");
                inputPasswordConfirm.setError("Password mismatch");
                hasError = true;
            }
            if (!hasError) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    user.updatePassword(inputPassword.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AuthResetPassword.this, "Successfully reset password", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AuthResetPassword.this, MainActivity.class));
                                } else {
                                    Toast.makeText(AuthResetPassword.this, "Failed to set new password", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(AuthResetPassword.this, "Failed to get session", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
