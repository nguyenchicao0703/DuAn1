package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.project1.R
import com.google.firebase.auth.FirebaseAuth

class AuthResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val inputPassword = findViewById<EditText>(R.id.resetPassword_edt_newPass)
        val inputPasswordConfirm = findViewById<EditText>(R.id.resetPassword_edt_confirmNewPass)

        findViewById<Button>(R.id.resetPassword_btn_save).setOnClickListener {
            var hasError = false
            if (inputPassword.text.toString().isEmpty()) {
                inputPassword.error = "Field must not be empty"
                hasError = true
            }
            if (
                inputPasswordConfirm.text.toString().isEmpty()) {
                inputPasswordConfirm.error = "Field must not be empty"
                hasError = true
            }
            if (inputPassword.text.toString() != inputPasswordConfirm.text.toString()) {
                inputPassword.error = "Password mismatch"
                inputPasswordConfirm.error = "Password mismatch"
                hasError = true
            }
            if (!hasError) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.updatePassword(inputPassword.text.toString())
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@AuthResetPassword,
                                "Successfully reset password",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@AuthResetPassword,
                                    AuthLoginActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this@AuthResetPassword,
                                "Failed to set new password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    ?: Toast.makeText(
                        this@AuthResetPassword,
                        "Failed to get session",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }
}