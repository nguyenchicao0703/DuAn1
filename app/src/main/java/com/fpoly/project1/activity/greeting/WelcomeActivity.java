package com.fpoly.project1.activity.greeting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.fpoly.project1.R;
import com.fpoly.project1.activity.MainActivity;
import com.fpoly.project1.activity.authentication.AuthLoginActivity;
import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.stream.Collectors;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_screen);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.splash));
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));

        Glide.with(this).load(R.mipmap.splash).into((ImageView) findViewById(R.id.welcome_iv_splash));

        //delay qua man hinh cho
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        new Handler().postDelayed(() -> {
            Intent intent;

            // check if user already launched the app once
            if (getSharedPreferences("firstLaunch", MODE_PRIVATE).getBoolean("seen", false)) {
                // check if user session is still available
                if (FirebaseAuth.getInstance().getCurrentUser() != null || (Profile.getCurrentProfile() != null && accessToken != null && !accessToken.isExpired())) {
                    // set session ID
                    Firebase.setSessionId(
                            new ControllerCustomer().getAllSync().stream().filter(customer ->
                                    customer.gid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            || customer.fid.equals(Profile.getCurrentProfile().getId())
                            ).collect(Collectors.toList()).get(0).__id
                    );
                    
                    // send to main screen
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                } else {
                    // send to login screen
                    intent = new Intent(WelcomeActivity.this, AuthLoginActivity.class);
                }
            } else {
                // send to introduction screen
                intent = new Intent(WelcomeActivity.this, IntroduceActivity.class);
            }

            startActivity(intent);
            finish();
        }, 3_000L);
    }
}