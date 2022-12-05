package com.fpoly.project1.activity.greeting

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.Profile
import com.fpoly.project1.R
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.activity.authentication.AuthLogin
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth
import java.util.stream.Collectors

class GreetingWelcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_splash)

        window.setBackgroundDrawable(resources.getDrawable(R.mipmap.splash))
        window.statusBarColor = resources.getColor(android.R.color.white)
        window.navigationBarColor = resources.getColor(android.R.color.white)

        Glide.with(this).load(R.mipmap.splash).into(findViewById(R.id.welcome_iv_splash))

        // delay qua man hinh cho
        val accessToken = AccessToken.getCurrentAccessToken()
        Handler().postDelayed({
            // check if user already launched the app once
            val intent: Intent =
                if (getSharedPreferences("firstLaunch", MODE_PRIVATE).getBoolean("seen", false)) {
                    // check if user session is still available
                    val hasFirebaseSession = FirebaseAuth.getInstance().currentUser != null
                    val hasFacebookSession =
                        Profile.getCurrentProfile() != null && accessToken != null && !accessToken.isExpired
                    if (hasFirebaseSession || hasFacebookSession) {
                        // set session ID
                        SessionUser.setId(
                            ControllerCustomer().getAllSync()!!.stream()
                                .filter { customer: Customer ->
                                    (
                                            customer.gid.equals(
                                                FirebaseAuth.getInstance().currentUser!!.uid
                                            ) ||
                                                    customer.fid.equals(Profile.getCurrentProfile()!!.id)
                                            )
                                }.collect(Collectors.toList())[0].id
                        )

                        // send to main screen
                        Intent(this@GreetingWelcome, MainActivity::class.java)
                    } else {
                        // send to login screen
                        Intent(this@GreetingWelcome, AuthLogin::class.java)
                    }
                } else {
                    // send to introduction screen
                    Intent(this@GreetingWelcome, GreetingIntroduction::class.java)
                }
            startActivity(intent)
            finish()
        }, 3_000L)
    }
}
