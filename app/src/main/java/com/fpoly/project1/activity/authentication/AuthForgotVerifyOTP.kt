package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.project1.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor

class AuthForgotVerifyOTP : AppCompatActivity() {
    private var verificationId: String? = null

    // verification callbacks
    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // ignored
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // ignored
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                this@AuthForgotVerifyOTP.verificationId = verificationId
                Toast.makeText(
                    this@AuthForgotVerifyOTP,
                    "Verification code had been sent to your phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    private var resendTxt: TextView? = null

    // timer tasks for resend verification code
    private var timerSeconds = 0
    private var timerObject: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_forgot_pwd_otp)

        // check if activity was launched correctly
        val requestPhoneNumber = intent.extras!!
            .getString("phoneNumber", null)
        if (requestPhoneNumber == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
            return
        }

        // handle user OTP input
        val inputOTPCode = findViewById<EditText>(R.id.forgotPassword_OTP_edt)
        val displayPhoneNumber = findViewById<TextView>(R.id.fotgotPassword_OTP_txt_sdt)
        displayPhoneNumber.text = requestPhoneNumber.replace(
            requestPhoneNumber.substring(
                floor(requestPhoneNumber.length * 0.5).toInt(),
                requestPhoneNumber.length - 1
            ),
            "*"
        )
        findViewById<Button>(R.id.forgotPassword_OTP_btn_next).setOnClickListener {
            if (inputOTPCode.text.toString().isEmpty()) {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // try to sign the user in with the OTP code
            val credential =
                PhoneAuthProvider.getCredential(verificationId!!, inputOTPCode.text.toString())

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    // this will log the user in, then switch to reset password activity
                    if (task.isSuccessful) {
                        startActivity(Intent(this@AuthForgotVerifyOTP, AuthResetPassword::class.java))
                        finish()
                    } else {
                        // otherwise code is invalid
                        Toast.makeText(
                            this@AuthForgotVerifyOTP,
                            "Invalid verification code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // send verification code on startup
        sendVerificationCode(requestPhoneNumber)

        // send verification code again
        resendTxt = findViewById(R.id.fotgotPassword_OTP_txt_time)
        resendTxt!!.setOnClickListener {
            sendVerificationCode(
                requestPhoneNumber
            )
        }
    }

    private fun sendVerificationCode(requestPhoneNumber: String) {
        // send verification code
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(requestPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
        )

        // prevent clicking
        resendTxt!!.isClickable = false

        // countdown task
        timerSeconds = 60

        // start the timer task
        timerObject = Timer()
        timerObject!!.schedule(
            object : TimerTask() {
                override fun run() {
                    if (timerSeconds > 0) {
                        resendTxt!!.text = "Resend code in ${--timerSeconds} seconds"
                    } else {
                        // cancel
                        timerObject!!.cancel()

                        // set back to normal
                        resendTxt!!.text = "Resend code"
                        resendTxt!!.isClickable = true
                    }
                }
            },
            0L, 1_000L
        )
    }
}
