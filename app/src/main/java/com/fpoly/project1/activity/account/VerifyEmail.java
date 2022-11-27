package com.fpoly.project1.activity.account;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        if (!user.isEmailVerified())
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();

                    task.getException().printStackTrace();
                }
            });
    }
}
