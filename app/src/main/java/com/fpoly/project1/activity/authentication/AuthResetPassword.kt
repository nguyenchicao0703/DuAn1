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
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_change_pwd)

        etPassword = findViewById(R.id.changePassword_edt_newPass)
        etPasswordConfirm = findViewById(R.id.changePassword_edt_confirmNewPass)

        findViewById<Button>(R.id.changePassword_btn_save).setOnClickListener {
            var hasError = false
            if (etPassword.text.toString().isEmpty()) {
                etPassword.error = "Field must not be empty"
                hasError = true
            }
            if (
                etPasswordConfirm.text.toString().isEmpty()
            ) {
                etPasswordConfirm.error = "Field must not be empty"
                hasError = true
            }
            if (etPassword.text.toString() != etPasswordConfirm.text.toString()) {
                etPassword.error = "Password mismatch"
                etPasswordConfirm.error = "Password mismatch"
                hasError = true
            }
            if (!hasError) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.updatePassword(etPassword.text.toString())
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
                                    AuthLogin::class.java
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
