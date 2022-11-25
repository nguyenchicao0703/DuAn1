package com.fpoly.project1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fpoly.project1.R;

public class IntroduceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set as already seen introduction
        getSharedPreferences("firstLaunch", MODE_PRIVATE).edit().putBoolean("seen", true).apply();

        setContentView(R.layout.activity_introduce);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.splash));
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        findViewById(R.id.btnNextIntroduce)
                .setOnClickListener(v -> startActivity(new Intent(IntroduceActivity.this, AuthActivity.class)));
    }
}