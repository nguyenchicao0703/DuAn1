package com.fpoly.project1.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerBase;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;
import com.fpoly.project1.firebase.service.ServiceCustomerHandler;
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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private final int REQ_CODE = 72;
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if user is already logged in
        // note: firebase keeps account state even after app exit so there's no need
        // for a "Remember me" checkbox, user can manually logout when they need to
        // with FirebaseAuth.getInstance().signOut()
        if (firebaseAuth.getCurrentUser() != null) {
            // TODO replace activity
            startActivity(new Intent(this, TestProfileActivity.class));
            finish();
        }

        // proceed with login activity
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // register btn
        findViewById(R.id.login_tv_signup)
                .setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // firebase related
        googleSignInClient = GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("796777611334-ft6vop9d5juh8m1bmgav4qa0ils5hedv.apps.googleusercontent.com") // TODO replace with resource
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
                        ).addOnCompleteListener(this::completeListener);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQ_CODE) return;

        Log.i("LoginActivity", "Received result");

        try {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            if (googleSignInAccount == null) {
                Toast.makeText(this, "Received Null while getting account", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth
                    .signInWithCredential(GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null))
                    .addOnCompleteListener(this::completeListener);
        } catch (ApiException apiException) {
            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            apiException.printStackTrace();
        }
    }

    private void completeListener(Task<AuthResult> task) {
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
                                    c -> c.emailAddress.equals(user.getEmail())
                            ).toArray();

                            // if there isn't any matching user, we create one
                            if (matchingCustomer.length == 0) {
                                Customer customerAccount =
                                        new Customer(
                                                null,
                                                user.getUid(),
                                                user.getPhotoUrl().toString(),
                                                user.getDisplayName(),
                                                null,
                                                user.getEmail(),
                                                null
                                        );

                                // add the user to firebase
                                controllerCustomer.newCustomer(customerAccount,
                                        new ControllerBase.SuccessListener() {
                                            @Override
                                            public void run() {
                                                Log.i("LoginActivity", "Added account to Firebase");

                                                // TODO replace activity
                                                startActivity(new Intent(LoginActivity.this, TestProfileActivity.class)
                                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                );
                                            }
                                        },
                                        new ControllerBase.FailureListener() {
                                            @Override
                                            public void run(Exception error) {
                                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                                Log.i("LoginActivity", "Failed to add account to Firebase");
                                                error.printStackTrace();
                                            }
                                        });
                            } else {
                                // if user is already exist
                                Log.i("LoginActivity", "Got account from Firebase");

                                startActivity(new Intent(LoginActivity.this, TestProfileActivity.class) // TODO replace me
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                );
                            }
                        }
                    },
                    new ControllerBase.FailureListener() {
                        @Override
                        public void run(Exception error) {
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            Log.i("LoginActivity", "Failed to get account from Firebase");
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
}