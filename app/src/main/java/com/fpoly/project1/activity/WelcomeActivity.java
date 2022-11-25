package com.fpoly.project1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fpoly.project1.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.splash));
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));

        ImageView ivLogo = findViewById(R.id.ivLogo);

        Glide.with(this).load(R.mipmap.splash).into(ivLogo);

        //delay qua man hinh cho
        new Handler().postDelayed(() ->
                        startActivity(new Intent(
                                WelcomeActivity.this,
                                getSharedPreferences("firstLaunch", MODE_PRIVATE).getBoolean("seen", false)
                                        ? AuthActivity.class
                                        : IntroduceActivity.class)
                        )
                , 3_000L);
    }
}