package com.fpoly.project1.activity.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.stream.Collectors;

public class AuthForgotPassword extends AppCompatActivity {
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // get email data
        Bundle requestData = getIntent().getExtras();
        String requestEmail = requestData.getString("email", null);

        // if email is invalid, stop the activity
        if (requestEmail == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // get account with matching email
            Customer matchingAccount = controllerCustomer.getAllSync().stream().filter(acc ->
                    acc.emailAddress.equals(requestEmail)).collect(Collectors.toList()
            ).get(0);

            // bindings
            CardView cardViewSms = findViewById(R.id.forgot_cardView_sms);
            TextView txtSms = findViewById(R.id.forgot_txt_sms);
            CardView cardViewEmail = findViewById(R.id.forgot_cardView_email);
            TextView txtEmail = findViewById(R.id.forgot_txt_email);

            // if phone number is available
            if (matchingAccount.phoneNumber != null) {
                txtSms.setText(matchingAccount.phoneNumber.replace(
                        matchingAccount.phoneNumber.substring(
                                (int) Math.floor(matchingAccount.phoneNumber.length() * 0.7),
                                matchingAccount.phoneNumber.length() - 1
                        ),
                        "*"
                ));

                // start the verify OTP activity
                cardViewSms.setOnClickListener(v -> {
                    Bundle requestBundle = new Bundle();
                    requestBundle.putString("phoneNumber", matchingAccount.phoneNumber);

                    Intent requestIntent = new Intent(this, AuthForgotVerifyOTP.class);
                    requestIntent.putExtras(requestBundle);

                    startActivity(requestIntent);
                });
            } else {
                // phone number not available
                cardViewSms.setVisibility(View.GONE);
            }

            // if email is available, which in most case is
            // this check may be removed later
            if (matchingAccount.emailAddress != null) {
                String emailUsername = matchingAccount.emailAddress.split("@")[0];

                txtEmail.setText(emailUsername.replace(
                        emailUsername.substring(
                                (int) Math.floor(emailUsername.length() * 0.7),
                                emailUsername.length() - 1
                        ),
                        "*"
                ));

                // send reset password email
                // TODO unknown how Firebase handle the reset password process, need to clarify
                cardViewEmail.setOnClickListener(v -> {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(matchingAccount.emailAddress)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Recovery email had been sent, please check your inbox", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to send recovery email", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            } else {
                // email not available, which is very unlikely
                cardViewEmail.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to find account with that email address", Toast.LENGTH_SHORT).show();
        }
    }
}
