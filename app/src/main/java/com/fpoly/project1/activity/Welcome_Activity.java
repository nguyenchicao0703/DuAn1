package com.fpoly.project1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fpoly.project1.R;

public class Welcome_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setBackgroundDrawable(this.getResources().getDrawable(R.mipmap.splash));
        getWindow().setStatusBarColor(this.getResources().getColor(android.R.color.white));
        getWindow().setNavigationBarColor(this.getResources().getColor(android.R.color.white));

        ImageView ivLogo = findViewById(R.id.ivLogo);

        Glide.with(this).load(R.mipmap.splash).into(ivLogo);

        //delay qua man hinh cho
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Welcome_Activity.this, LoginActivity.class));
            }
        }, 3000);
    }
}