package com.fpoly.project1.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fpoly.project1.R;
import com.fpoly.project1.firebase.Firebase;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class GoogleSignInActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        // check if user is already logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(this, TestProfileActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
            finish();
        }

        // proceed with login screen
        setContentView(R.layout.test_act_login);

        // assign button here
        SignInButton btnSignIn = findViewById(R.id.test_btn_signin);

        // setup new client
        googleSignInClient = GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Resources.getSystem().getString(R.string.web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()
        );

        // btn listener
        btnSignIn.setOnClickListener(v -> startActivityForResult(googleSignInClient.getSignInIntent(), 72));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 72) return;

        Log.i("Google", "Received result");

        try {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            if (googleSignInAccount == null) return;

            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

            firebaseAuth
                    .signInWithCredential(authCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(this, TestProfileActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            );
                        } else {
                            Toast.makeText(
                                    this,
                                    "Auth failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        } catch (ApiException apiException) {
            apiException.printStackTrace();
        }
    }
}
