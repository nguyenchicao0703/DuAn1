package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.Profile
import com.fpoly.project1.R
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth

class AuthFillBio : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var etFullName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etAddress: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_register_fill_bio)

        etFullName = findViewById(R.id.registerProfile_edt_fullName)
        etBirthDate = findViewById(R.id.registerProfile_edt_date)
        etPhoneNumber = findViewById(R.id.registerProfile_edt_phoneNumber)
        etAddress = findViewById(R.id.registerProfile_edt_address)

        val google = FirebaseAuth.getInstance().currentUser!! // via password or google login
        val facebook = Profile.getCurrentProfile() // via facebook login

        etFullName.setText(google.displayName)
        etPhoneNumber.setText(google.phoneNumber)

        findViewById<ImageView>(R.id.registerProfile_iv_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.registerProfile_btn_next).setOnClickListener {
            val account = Customer(
                null,
                google.uid,
                facebook?.id,
                google.photoUrl.toString(),
                etFullName.text.toString(),
                etBirthDate.text.toString(),
                google.email,
                etPhoneNumber.text.toString(),
                etAddress.text.toString(),
                null
            )

            controllerCustomer.addAsync(account,
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(unused: Any?) {
                        val sessionId = unused as String?

                        if (sessionId != null) {
                            Toast.makeText(
                                this@AuthFillBio, "Successfully recorded user details", Toast
                                    .LENGTH_SHORT
                            ).show()

                            SessionUser.setId(sessionId)

                            startActivity(Intent(this@AuthFillBio, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@AuthFillBio,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                failureListener = object : ControllerBase.FailureListener() {
                    override fun run(error: Exception?) {
                        Toast.makeText(
                            this@AuthFillBio, "Failed to save data",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.e(this@AuthFillBio::class.simpleName, "Error", error)
                    }
                }
            )
        }
    }
}
