package com.fpoly.project1.activity.greeting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.project1.R
import com.fpoly.project1.activity.authentication.AuthLogin

class GreetingIntroduction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduce)

        // set as already seen introduction
        getSharedPreferences("firstLaunch", MODE_PRIVATE).edit().putBoolean("seen", true).apply()

        window.setBackgroundDrawable(resources.getDrawable(R.mipmap.splash))
        window.statusBarColor = resources.getColor(android.R.color.black)
        window.navigationBarColor = resources.getColor(android.R.color.black)

        findViewById<Button>(R.id.introduce_btn_next)
            .setOnClickListener {
                startActivity(
                    Intent(
                        this@GreetingIntroduction,
                        AuthLogin::class.java
                    )
                )
            }
    }
}