package com.fpoly.project1.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fpoly.project1.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;

public class TestProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act_profile);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into((ImageView) findViewById(R.id.test_profile_avatar));
            ((TextView) findViewById(R.id.test_profile_gid)).setText(firebaseUser.getUid());
            ((TextView) findViewById(R.id.test_profile_email)).setText(firebaseUser.getEmail());
        }

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        findViewById(R.id.test_profile_logout).setOnClickListener(view -> {
            // Sign out from google
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                // Check condition
                if (task.isSuccessful()) {
                    // When task is successful
                    // Sign out from firebase
                    FirebaseAuth.getInstance().signOut();

                    // Display Toast
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();

                    // Finish activity
                    finish();
                }
            });
        });
    }
}
