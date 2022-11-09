package com.fpoly.project1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fpoly.project1.R;

public class IntroduceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        getWindow().setBackgroundDrawable(this.getResources().getDrawable(R.mipmap.splash));
        getWindow().setStatusBarColor(this.getResources().getColor(android.R.color.black));
        getWindow().setNavigationBarColor(this.getResources().getColor(android.R.color.black));

        Button btnNextIntroduce = findViewById(R.id.btnNextIntroduce);
        btnNextIntroduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroduceActivity.this, LoginActivity.class));
            }
        });
    }
}