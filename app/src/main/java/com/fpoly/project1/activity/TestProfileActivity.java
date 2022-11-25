package com.fpoly.project1.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class TestProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act_profile);

        ControllerCustomer controllerCustomer = new ControllerCustomer();
        String email = getSharedPreferences("cheetah", Context.MODE_PRIVATE).getString("email", null);
        controllerCustomer.getAllSync().forEach(c -> {

            if (c.emailAddress.equals(email)) {
                Glide.with(TestProfileActivity.this).load(c.avatarUrl).into((ImageView) findViewById(R.id.test_profile_avatar));
                ((TextView) findViewById(R.id.test_profile_gid)).setText(c.__id);
                ((TextView) findViewById(R.id.test_profile_email)).setText(c.fullName);
            }
        });

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        findViewById(R.id.test_profile_logout).setOnClickListener(view -> {
            LoginManager.getInstance().logOut();

            googleSignInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                }
            });

            finish();
        });
    }
}
