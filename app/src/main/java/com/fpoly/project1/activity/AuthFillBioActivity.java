package com.fpoly.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.Profile;
import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthFillBioActivity extends AppCompatActivity {
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        EditText inputFullName = findViewById(R.id.registerProfile_edt_fullName);
        EditText inputBirthDate = findViewById(R.id.registerProfile_edt_date);
        EditText inputPhoneNumber = findViewById(R.id.registerProfile_edt_phoneNumber);
        EditText inputAddress = findViewById(R.id.registerProfile_edt_address);

        Customer account; // base object
        FirebaseUser google = FirebaseAuth.getInstance().getCurrentUser(); // via password or google login
        Profile facebook = Profile.getCurrentProfile(); // via facebook login

        if (facebook != null) {
            inputFullName.setText(facebook.getName());

            account = new Customer(
                    null,
                    null,
                    facebook.getId(),
                    facebook.getProfilePictureUri(500, 500).toString(),
                    inputFullName.getText().toString(),
                    inputBirthDate.getText().toString(),
                    null,
                    inputAddress.getText().toString(),
                    null
            );
        } else {
            inputFullName.setText(google.getDisplayName());
            inputPhoneNumber.setText(google.getPhoneNumber());

            account = new Customer(
                    null,
                    google.getUid(),
                    null,
                    google.getPhotoUrl().toString(),
                    inputFullName.getText().toString(),
                    inputBirthDate.getText().toString(),
                    google.getEmail(),
                    inputAddress.getText().toString(),
                    null
            );
        }

        findViewById(R.id.registerProfile_btn_next).setOnClickListener(v -> {
            if (controllerCustomer.setSync(account, false)) {
                Toast.makeText(this, "Successfully recorded user details", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
