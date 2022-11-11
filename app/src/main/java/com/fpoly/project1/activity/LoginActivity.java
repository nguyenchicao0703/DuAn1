package com.fpoly.project1.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerBase;
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
import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private final int REQ_CODE = 72;
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();

        // check if user is already logged in
        // note: firebase and facebook keeps account state even after app exit so there's no need
        // for a "Remember me" checkbox, user can manually logout when they need to
        // with FirebaseAuth.getInstance().signOut() or LoginManager.getInstance().logOut()
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (firebaseAuth.getCurrentUser() != null || // google auth status
                (Profile.getCurrentProfile() != null // facebook auth status
                        && accessToken != null
                        && !accessToken.isExpired())
        ) {
            startProfileActivity(getSharedPreferences("cheetah", Context.MODE_PRIVATE).getString("email", null));
        }

        // proceed with login activity
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // register btn
        findViewById(R.id.login_tv_signup)
                .setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

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
        findViewById(R.id.login_btn_google)
                .setOnClickListener(v -> startActivityForResult(googleSignInClient.getSignInIntent(), REQ_CODE));

        // login via email and password
        findViewById(R.id.login_btn_login)
                .setOnClickListener(v -> {
                    boolean hasError = false;
                    EditText etEmail = findViewById(R.id.login_et_email);
                    EditText etPass = findViewById(R.id.login_et_password);

                    if (etEmail.getText().toString().isEmpty()) {
                        etEmail.setError("Email must not be empty");
                        hasError = true;
                    }

                    if (etPass.getText().toString().isEmpty()) {
                        etPass.setError("Password must not be empty");
                        hasError = true;
                    }

                    if (!hasError) {
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
                Toast.makeText(LoginActivity.this, "User cancelled action", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        });

        // login via facebook button
        findViewById(R.id.login_btn_facebook)
                .setOnClickListener(v -> {
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                });
    }

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
            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            apiException.printStackTrace();
        }
    }

    private void googleCompleteListener(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            controllerCustomer.getAllCustomer(
                    new ControllerBase.SuccessListener() {
                        @Override
                        public void run(DataSnapshot dataSnapshot) {
                            List<Customer> customers = new ArrayList<>();

                            // null snapshot
                            if (dataSnapshot.getValue() == null) {
                                Toast.makeText(LoginActivity.this, "Snapshot does not exist", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // add list of customers
                            // on a large scale, this will have a performance hit
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                customers.add(ds.getValue(Customer.class));
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

                            // if there isn't any matching user, we create one
                            if (matchingCustomer.length == 0) {
                                Customer customerAccount =
                                        new Customer(
                                                null,
                                                user.getUid(),
                                                null,
                                                user.getPhotoUrl().toString(),
                                                user.getDisplayName(),
                                                null,
                                                user.getEmail(),
                                                null
                                        );

                                // add the user to firebase
                                controllerCustomer.setCustomer(customerAccount,
                                        false,
                                        new ControllerBase.SuccessListener() {
                                            @Override
                                            public void run() {
                                                Log.i("LoginActivity::Google", "Added account to Firebase");

                                                startProfileActivity(customerAccount.emailAddress);
                                            }
                                        },
                                        new ControllerBase.FailureListener() {
                                            @Override
                                            public void run(Exception error) {
                                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                                Log.e("LoginActivity::Google", "Failed to add account to Firebase");
                                                error.printStackTrace();
                                            }
                                        });
                            } else {
                                // if user is already exist
                                Log.i("LoginActivity::Google", "Got account from Firebase");

                                startProfileActivity(((Customer) matchingCustomer[0]).emailAddress);
                            }
                        }
                    },
                    new ControllerBase.FailureListener() {
                        @Override
                        public void run(Exception error) {
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            Log.e("LoginActivity::Google", "Failed to get account from Firebase");
                            error.printStackTrace();
                        }
                    });
        } else {
            Objects.requireNonNull(task.getException()).printStackTrace();

            Toast.makeText(
                    this,
                    "Auth failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void facebookCompleteListener(LoginResult loginResult) {
        controllerCustomer.getAllCustomer(
                new ControllerBase.SuccessListener() {
                    @Override
                    public void run(DataSnapshot dataSnapshot) {
                        List<Customer> customers = new ArrayList<>();

                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(LoginActivity.this, "Snapshot does not exist", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            customers.add(ds.getValue(Customer.class));
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
                                            Customer customerAccount =
                                                    new Customer(
                                                            null,
                                                            null,
                                                            profile.getId(),
                                                            profile.getProfilePictureUri(500, 500).toString(),
                                                            profile.getName(),
                                                            null,
                                                            email,
                                                            null
                                                    );

                                            // add the user to firebase
                                            controllerCustomer.setCustomer(customerAccount,
                                                    false,
                                                    new ControllerBase.SuccessListener() {
                                                        @Override
                                                        public void run() {
                                                            Log.i("LoginActivity::Facebook", "Added account to Firebase");

                                                            startProfileActivity(customerAccount.emailAddress);
                                                        }
                                                    },
                                                    new ControllerBase.FailureListener() {
                                                        @Override
                                                        public void run(Exception error) {
                                                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                                            Log.e("LoginActivity::Facebook", "Failed to add account to Firebase");
                                                            error.printStackTrace();
                                                        }
                                                    });
                                        } else {
                                            // if user is already exist
                                            Log.i("LoginActivity::Facebook", "Got account from Firebase");

                                            startProfileActivity(((Customer) matchingCustomer[0]).emailAddress);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                        Bundle bundle = new Bundle();
                        bundle.putString("fields", "id,name,email");
                        request.setParameters(bundle);
                        request.executeAsync();
                    }
                },
                new ControllerBase.FailureListener() {
                    @Override
                    public void run(Exception error) {
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        Log.e("LoginActivity::Facebook", "Failed to get account from Firebase");
                        error.printStackTrace();
                    }
                });
    }

    private void startProfileActivity(String email) {
        Intent intent = new Intent(LoginActivity.this, TestProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        intent.putExtras(bundle);
        
        SharedPreferences.Editor editor = getSharedPreferences("cheetah", Context.MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.apply();

        startActivity(intent);
    }
}