package com.fpoly.project1.activity.account

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AccountVerifyEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser!!

        if (!user.isEmailVerified) user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent to " + user.email, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT)
                        .show()
                    task.exception!!.printStackTrace()
                }
            }
    }
}
