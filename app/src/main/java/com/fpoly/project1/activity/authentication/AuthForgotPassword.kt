package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.fpoly.project1.R
import com.fpoly.project1.activity.authenticationimport.AuthForgotVerifyOTP
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth
import java.util.stream.Collectors
import kotlin.math.floor

class AuthForgotPassword : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // get email data
        val requestData = intent.extras
        val requestEmail = requestData!!.getString("email", null)

        // if email is invalid, stop the activity
        if (requestEmail == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            // get account with matching email
            val matchingAccount = controllerCustomer.getAllSync()!!.stream()
                .filter { acc: Customer -> acc.emailAddress == requestEmail }
                .collect(
                    Collectors.toList()
                )[0]

            // bindings
            val cardViewSms = findViewById<CardView>(R.id.forgot_cardView_sms)
            val txtSms = findViewById<TextView>(R.id.forgot_txt_sms)
            val cardViewEmail = findViewById<CardView>(R.id.forgot_cardView_email)
            val txtEmail = findViewById<TextView>(R.id.forgot_txt_email)

            // if phone number is available
            if (matchingAccount?.phoneNumber != null) {
                val phoneNumber = matchingAccount.phoneNumber!!
                txtSms.text = phoneNumber.replace(
                    phoneNumber.substring(
                        floor(phoneNumber.length.times(0.7)).toInt(),
                        phoneNumber.length - 1
                    ),
                    "*"
                )

                // start the verify OTP activity
                cardViewSms.setOnClickListener {
                    val requestBundle = Bundle()
                    requestBundle.putString("phoneNumber", matchingAccount.phoneNumber)

                    val requestIntent = Intent(this, AuthForgotVerifyOTP::class.java)
                    requestIntent.putExtras(requestBundle)

                    startActivity(requestIntent)
                }
            } else {
                // phone number not available
                cardViewSms.visibility = View.GONE
            }

            // if email is available, which in most case is
            // this check may be removed later
            if (matchingAccount?.emailAddress != null) {
                val emailUsername =
                    matchingAccount.emailAddress!!.split("@".toRegex()).toTypedArray()[0]

                txtEmail.text = emailUsername.replace(
                    emailUsername.substring(
                        floor(emailUsername.length * 0.7).toInt(),
                        emailUsername.length - 1
                    ),
                    "*"
                )

                // send reset password email
                // TODO unknown how Firebase handle the reset password process, need to clarify
                cardViewEmail.setOnClickListener {
                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(matchingAccount.emailAddress!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Recovery email had been sent, please check your inbox",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to send recovery email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } else {
                // email not available, which is very unlikely
                cardViewEmail.visibility = View.GONE
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Unable to find account with that email address",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}