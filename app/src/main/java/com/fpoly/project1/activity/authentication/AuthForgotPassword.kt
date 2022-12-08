package com.fpoly.project1.activity.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.fpoly.project1.R
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import kotlin.math.floor
import kotlin.math.roundToInt

class AuthForgotPassword : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_forgot_pwd)

        findViewById<ImageView>(R.id.forgot_iv_back).setOnClickListener { finish() }

        // get email data
        val requestEmail = intent.extras!!.getString("email", null)

        // if email is invalid, stop the activity
        if (requestEmail == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
            return
        }

        controllerCustomer.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                @SuppressLint("SetTextI18n")
                override fun run(dataSnapshot: DataSnapshot?) {
                    var matchingAccount: Customer? = null
                    dataSnapshot?.children?.forEach {
                        if (it.getValue(Customer::class.java)!!.emailAddress!! == requestEmail.trim()) {
                            matchingAccount = it.getValue(Customer::class.java)!!
                        }
                    }

                    if (matchingAccount == null) {
                        Toast.makeText(
                            this@AuthForgotPassword, "Unable to find matching " +
                                    "account", Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    // bindings
                    val cardViewSms = findViewById<CardView>(R.id.forgot_cardView_sms)
                    val txtSms = findViewById<TextView>(R.id.forgot_txt_sms)
                    val cardViewEmail = findViewById<CardView>(R.id.forgot_cardView_email)
                    val txtEmail = findViewById<TextView>(R.id.forgot_txt_email)

                    // if phone number is available
                    if (matchingAccount!!.phoneNumber?.isEmpty() != true) {
                        val phoneNumber = matchingAccount!!.phoneNumber!!
                        txtSms.text = phoneNumber.substring(0, floor(phoneNumber.length * 0.5).toInt())
                            .padEnd(phoneNumber.length, '*')


                        // start the verify OTP activity
                        cardViewSms.setOnClickListener {
                            val requestBundle = Bundle()
                            requestBundle.putString("phoneNumber", matchingAccount!!.phoneNumber)

                            val requestIntent =
                                Intent(this@AuthForgotPassword, AuthForgotVerifyOTP::class.java)
                            requestIntent.putExtras(requestBundle)

                            startActivity(requestIntent)
                        }
                    } else {
                        // phone number not available
                        cardViewSms.visibility = View.GONE
                    }

                    // if email is available, which in most case is
                    // this check may be removed later
                    if (matchingAccount!!.emailAddress != null) {
                        val emailUsername =
                            matchingAccount!!.emailAddress!!.split("@".toRegex()).toTypedArray()[0]
                        val emailWorkplace =
                            matchingAccount!!.emailAddress!!.split("@".toRegex()).toTypedArray()[1]

                        txtEmail.text = "${
                            emailUsername.substring(0, floor(emailUsername.length * 0.5).toInt())
                                .padEnd(emailUsername.length, '*')
                        }@${emailWorkplace}"

                        // send reset password email
                        // TODO unknown how Firebase handle the reset password process, need to clarify
                        cardViewEmail.setOnClickListener {
                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(matchingAccount!!.emailAddress!!)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@AuthForgotPassword,
                                            "Recovery email had been sent, please check your inbox",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@AuthForgotPassword,
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
                }
            },
            failureListener = object : ControllerBase.FailureListener() {
                override fun run(error: Exception?) {
                    Toast.makeText(
                        this@AuthForgotPassword, "Failed to retrieve data from " +
                                "server", Toast.LENGTH_SHORT
                    ).show()

                    Log.e(this@AuthForgotPassword::class.simpleName, "Error", error)
                }
            }
        )
    }
}
