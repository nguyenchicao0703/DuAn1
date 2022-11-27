package com.fpoly.project1.activity.greeting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.authentication.AuthLoginActivity;

public class IntroduceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        // set as already seen introduction
        getSharedPreferences("firstLaunch", MODE_PRIVATE).edit().putBoolean("seen", true).apply();

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.mipmap.splash));
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        findViewById(R.id.introduce_btn_next)
                .setOnClickListener(v -> startActivity(new Intent(IntroduceActivity.this, AuthLoginActivity.class)));
    }
}