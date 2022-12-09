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
import androidx.core.os.bundleOf
import com.fpoly.project1.R
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import kotlin.math.floor

class AuthForgotPassword : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var backButton: ImageView
    private lateinit var cardViewSms: CardView
    private lateinit var cardViewEmail: CardView
    private lateinit var txtSms: TextView
    private lateinit var txtEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_forgot_pwd)

        backButton = findViewById<ImageView>(R.id.forgot_iv_back)
        backButton.setOnClickListener { finish() }

        // bindings
        cardViewSms = findViewById(R.id.forgot_cardView_sms)
        txtSms = findViewById(R.id.forgot_txt_sms)
        cardViewEmail = findViewById(R.id.forgot_cardView_email)
        txtEmail = findViewById(R.id.forgot_txt_email)

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

                    // if phone number is available
                    if (matchingAccount!!.phoneNumber?.isEmpty() != true) {
                        val phoneNumber = matchingAccount!!.phoneNumber!!
                        txtSms.text =
                            phoneNumber.substring(0, floor(phoneNumber.length * 0.5).toInt())
                                .padEnd(phoneNumber.length, '*')

                        // start the verify OTP activity
                        cardViewSms.setOnClickListener {
                            val requestIntent =
                                Intent(this@AuthForgotPassword, AuthForgotVerifyOTP::class.java)
                            requestIntent.putExtras(
                                bundleOf(Pair("phoneNumber", matchingAccount!!.phoneNumber))
                            )

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
