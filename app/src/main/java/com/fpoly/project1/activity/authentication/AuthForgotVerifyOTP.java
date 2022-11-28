package com.fpoly.project1.activity.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fpoly.project1.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AuthForgotVerifyOTP extends AppCompatActivity {
    private String verificationId;
    // verification callbacks
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // ignored
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // ignored
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            AuthForgotVerifyOTP.this.verificationId = verificationId;

            Toast.makeText(AuthForgotVerifyOTP.this, "Verification code had been sent to your phone number", Toast.LENGTH_SHORT).show();
        }
    };
    private TextView resendTxt;
    // timer tasks for resend verification code
    private int timerSeconds = 0;
    private Timer timerObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_otp_activity);

        // check if activity was launched correctly
        String requestPhoneNumber = getIntent().getExtras().getString("phoneNumber", null);
        if (requestPhoneNumber == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            return;
        }

        // handle user OTP input
        EditText inputOTPCode = findViewById(R.id.forgotPassword_OTP_edt);
        TextView displayPhoneNumber = findViewById(R.id.fotgotPassword_OTP_txt_sdt);
        displayPhoneNumber.setText(
                requestPhoneNumber.replace(
                        requestPhoneNumber.substring(
                                (int) Math.floor(requestPhoneNumber.length() * 0.7),
                                requestPhoneNumber.length() - 1
                        ),
                        "*"
                )
        );

        findViewById(R.id.forgotPassword_OTP_btn_next).setOnClickListener(v -> {
            if (inputOTPCode.getText().toString().length() == 0) {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // try to sign the user in with the OTP code
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, inputOTPCode.getText().toString());
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
                // this will log the user in, then switch to reset password activity
                if (task.isSuccessful()) {
                    startActivity(new Intent(AuthForgotVerifyOTP.this, AuthResetPassword.class));
                } else {
                    // otherwise code is invalid
                    Toast.makeText(AuthForgotVerifyOTP.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // send verification code on startup
        sendVerificationCode(requestPhoneNumber);

        // send verification code again
        resendTxt = findViewById(R.id.fotgotPassword_OTP_txt_time);
        resendTxt.setOnClickListener(v -> sendVerificationCode(requestPhoneNumber));
    }

    private void sendVerificationCode(String requestPhoneNumber) {
        // send verification code
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(requestPhoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build()
        );

        // prevent clicking
        resendTxt.setClickable(false);

        // countdown task
        timerSeconds = 60;

        // start the timer task
        timerObject = new Timer();
        timerObject.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timerSeconds > 0) {
                    resendTxt.setText("Resend code in " + --timerSeconds + " seconds");
                } else {
                    // cancel
                    timerObject.cancel();

                    // set back to normal
                    resendTxt.setText("Resend code");
                    resendTxt.setClickable(true);
                }
            }
        }, 0L, 1_000L);
    }
}
