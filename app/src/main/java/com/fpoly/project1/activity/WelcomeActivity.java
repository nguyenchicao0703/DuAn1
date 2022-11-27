package com.fpoly.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.fpoly.project1.R;
import com.google.firebase.auth.FirebaseAuth;

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
        }, 3_000L);
    }
}