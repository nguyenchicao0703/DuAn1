package com.fpoly.project1.activity.authentication;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fpoly.project1.R;
import com.fpoly.project1.activity.MainActivity;
import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AuthLoginActivity extends AppCompatActivity {
    public static Resources resources;
    private final int REQ_CODE = 72;
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for use by Firebase
        resources = getResources();

        // proceed with the activity
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // switching between login and signup btn
        findViewById(R.id.auth_tv_switch).setOnClickListener(v -> {
            startActivity(new Intent(this, AuthRegisterActivity.class));
            finish();
        });

        // ============================ GOOGLE AUTHENTICATION ============================
        // firebase related
        googleSignInClient = GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.firebase_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()
        );

        // login via google button
        findViewById(R.id.auth_btn_google)
                .setOnClickListener(v -> startActivityForResult(googleSignInClient.getSignInIntent(), REQ_CODE));

        // login via email and password
        EditText etEmail = findViewById(R.id.auth_et_email);
        EditText etPass = findViewById(R.id.auth_et_password);
        findViewById(R.id.auth_btn_submit)
                .setOnClickListener(v -> {
                    boolean hasError = false;

                    if (etEmail.getText().toString().isEmpty()) {
                        etEmail.setError("Email must not be empty");
                        hasError = true;
                    }

                    if (etPass.getText().toString().isEmpty()) {
                        etPass.setError("Password must not be empty");
                        hasError = true;
                    }

                    if (!hasError) {
                        // sign in with email and password
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                                etEmail.getText().toString(),
                                etPass.getText().toString()
                        ).addOnCompleteListener(this::googleCompleteListener);
                    }
                });

        // ============================ FACEBOOK AUTHENTICATION ============================
        // facebook CallbackManager Factory
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookCompleteListener(loginResult);
            }

            @Override
            public void onCancel() {
                Toast.makeText(AuthLoginActivity.this, "User cancelled action", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(AuthLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        });

        // login via facebook button
        findViewById(R.id.auth_btn_facebook)
                .setOnClickListener(v -> {
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                });
    }

    // Listen for activity result from Google auth popup and Facebook auth screen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // facebook auth
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // google auth
        if (requestCode != REQ_CODE) return;

        try {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            if (googleSignInAccount == null) {
                Toast.makeText(this, "Received Null while getting account", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth
                    .signInWithCredential(GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null))
                    .addOnCompleteListener(this::googleCompleteListener);
        } catch (ApiException apiException) {
            Toast.makeText(AuthLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            apiException.printStackTrace();
        }
    }

    // Google auth complete listener
    private void googleCompleteListener(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            ArrayList<Customer> customers = controllerCustomer.getAllSync();
            if (customers == null) {
                Toast.makeText(this, "Failed to get users from database", Toast.LENGTH_SHORT).show();
                return;
            }

            // we will log user in via firebase since it include both
            // password-based auth and google auth,
            // here we're just creating a customer object linked to
            // the firebase account
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;

            // get matching account
            Object[] matchingCustomer = customers.stream().filter(
                    c -> c.emailAddress.equals(user.getEmail()) || c.gid.equals(user.getUid())
            ).toArray();

            // if there isn't any matching user, switch to fill bio activity
            if (matchingCustomer.length == 0) {
                startActivity(new Intent(this, AuthFillBioActivity.class));
            } else {
                // if user is already exist
                Log.i("LoginActivity::Google", "Got account from Firebase");

                Firebase.setSessionId(((Customer) matchingCustomer[0]).__id);
                startActivity(new Intent(AuthLoginActivity.this, MainActivity.class));
            }
        } else {
            Objects.requireNonNull(task.getException()).printStackTrace();

            Toast.makeText(
                    this,
                    "Auth failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Facebook auth complete listener
    private void facebookCompleteListener(LoginResult loginResult) {
        ArrayList<Customer> customers = controllerCustomer.getAllSync();
        if (customers == null) {
            Toast.makeText(this, "Failed to get users from database", Toast.LENGTH_SHORT).show();
            return;
        }

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (jsonObject, graphResponse) -> {
                    Log.i("LoginActivity::Facebook", graphResponse.toString());

                    try {
                        Profile profile = Profile.getCurrentProfile();
                        String email = jsonObject.getString("email");

                        Object[] matchingCustomer = customers.stream().filter(
                                c -> c.emailAddress.equals(email) || c.fid.equals(profile.getId())
                        ).toArray();

                        if (matchingCustomer.length == 0) {
                            startActivity(new Intent(this, AuthFillBioActivity.class));
                        } else {
                            // if user is already exist
                            Log.i("LoginActivity::Facebook", "Got account from Firebase");

                            Firebase.setSessionId(((Customer) matchingCustomer[0]).__id);
                            startActivity(new Intent(AuthLoginActivity.this, MainActivity.class));
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AuthLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,email");
        request.setParameters(bundle);
        request.executeAsync();
    }
}