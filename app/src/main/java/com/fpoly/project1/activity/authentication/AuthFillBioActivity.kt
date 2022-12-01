package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.Profile
import com.fpoly.project1.R
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth

class AuthFillBioActivity : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_profile)

        val inputFullName = findViewById<EditText>(R.id.registerProfile_edt_fullName)
        val inputBirthDate = findViewById<EditText>(R.id.registerProfile_edt_date)
        val inputPhoneNumber = findViewById<EditText>(R.id.registerProfile_edt_phoneNumber)
        val inputAddress = findViewById<EditText>(R.id.registerProfile_edt_address)

        val google = FirebaseAuth.getInstance().currentUser!! // via password or google login
        val facebook = Profile.getCurrentProfile() // via facebook login

        inputFullName.setText(google.displayName)
        inputPhoneNumber.setText(google.phoneNumber)

        val account = Customer(
            null,
            google.uid,
            facebook?.id,
            google.photoUrl.toString(),
            inputFullName.text.toString(),
            inputBirthDate.text.toString(),
            google.email,
            inputPhoneNumber.text.toString(),
            inputAddress.text.toString(),
            null
        )

        findViewById<Button>(R.id.registerProfile_btn_next).setOnClickListener {
            val sessionId = controllerCustomer.addSync(account)

            if (sessionId != null) {
                Toast.makeText(this, "Successfully recorded user details", Toast.LENGTH_SHORT)
                    .show()
                SessionUser.setId(sessionId)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}