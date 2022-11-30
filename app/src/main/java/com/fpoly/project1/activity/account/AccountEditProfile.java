package com.fpoly.project1.activity.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fpoly.project1.R;
import com.fpoly.project1.firebase.SessionUser;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;

public class AccountEditProfile extends AppCompatActivity {
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private EditText profileName;
    private EditText profileEmail;
    private EditText profilePhone;
    private EditText profileBday;
    private EditText profileAddress;

    private void updateFields(@Nullable Customer account) {
        if (account == null) account = controllerCustomer.getSync(SessionUser.sessionId);

        profileName.setText(account.fullName);
        profileEmail.setText(account.emailAddress);
        profilePhone.setText(account.phoneNumber);
        profileBday.setText(account.birthDate);
        profileAddress.setText(account.postalAddress);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_profile);

        Customer account = controllerCustomer.getSync(SessionUser.sessionId);
        Button updateProfile = findViewById(R.id.edit_profile_btn_save_changes);

        profileName = findViewById(R.id.edit_profile_txt_name);
        profileEmail = findViewById(R.id.edit_profile_txt_email);
        profilePhone = findViewById(R.id.edit_profile_txt_phoneNumber);
        profileBday = findViewById(R.id.edit_profile_txt_birthDate);
        profileAddress = findViewById(R.id.edit_profile_txt_address);

        // update edit text fields
        updateFields(account);

        // Full name
        profileName.setOnFocusChangeListener((view, state) ->
                updateProfile.setVisibility(
                        profileName.getText().toString().equals(account.fullName) ?
                                View.GONE : View.VISIBLE
                )
        );

        ImageView profileNameToggle = findViewById(R.id.edit_profile_iv_edit_name);
        profileNameToggle.setOnClickListener(v ->
                profileName.setEnabled(!profileName.isEnabled())
        );

        // Email address
        profileEmail.setOnFocusChangeListener((view, state) ->
                updateProfile.setVisibility(
                        profileEmail.getText().toString().equals(account.emailAddress) ?
                                View.GONE : View.VISIBLE
                )
        );

        ImageView profileEmailToggle = findViewById(R.id.edit_profile_iv_edit_email);
        profileEmailToggle.setOnClickListener(v ->
                profileEmail.setEnabled(!profileEmail.isEnabled())
        );

        // Phone number
        profilePhone.setOnFocusChangeListener((view, state) ->
                updateProfile.setVisibility(
                        profilePhone.getText().toString().equals(account.phoneNumber) ?
                                View.GONE : View.VISIBLE
                )
        );

        ImageView profilePhoneToggle = findViewById(R.id.edit_profile_iv_edit_phoneNumber);
        profilePhoneToggle.setOnClickListener(v ->
                profilePhone.setEnabled(!profilePhone.isEnabled())
        );

        // Birth date
        profileBday.setOnFocusChangeListener((view, state) ->
                updateProfile.setVisibility(
                        profileBday.getText().toString().equals(account.birthDate) ?
                                View.GONE : View.VISIBLE
                )
        );

        ImageView profileBdayToggle = findViewById(R.id.edit_profile_iv_edit_birthDate);
        profileBdayToggle.setOnClickListener(v ->
                profileBday.setEnabled(!profileBday.isEnabled())
        );

        // Postal Address
        profileAddress.setOnFocusChangeListener((view, state) ->
                updateProfile.setVisibility(
                        profileAddress.getText().toString().equals(account.postalAddress) ?
                                View.GONE : View.VISIBLE
                )
        );

        ImageView profileAddressToggle = findViewById(R.id.edit_profile_iv_edit_address);
        profileAddressToggle.setOnClickListener(v ->
                profileAddress.setEnabled(!profileAddress.isEnabled())
        );

        // update
        updateProfile.setOnClickListener(v -> {
            if (controllerCustomer.setSync(new Customer(
                    SessionUser.sessionId,
                    account.gid,
                    account.fid,
                    account.avatarUrl,
                    profileName.getText().toString(),
                    profileBday.getText().toString(),
                    profileEmail.getText().toString(),
                    profilePhone.getText().toString(),
                    profileAddress.getText().toString(),
                    account.favoriteIds
            ), true)) {
                Toast.makeText(this, "Successfully updated profile", Toast.LENGTH_SHORT).show();

                updateFields(null);
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
