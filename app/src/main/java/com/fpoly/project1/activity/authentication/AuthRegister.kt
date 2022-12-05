package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.project1.R
import com.google.firebase.auth.FirebaseAuth

class AuthRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_register)

        val inputEmail = findViewById<EditText>(R.id.register_edt_email)
        val inputPassword = findViewById<EditText>(R.id.register_edt_password)
        val inputPasswordConfirm = findViewById<EditText>(R.id.register_edt_confirm_password)

        findViewById<TextView>(R.id.register_txt_signIn).setOnClickListener {
            startActivity(Intent(this@AuthRegister, AuthLogin::class.java))
            finish()
        }

        findViewById<Button>(R.id.register_btn_signUp)
            .setOnClickListener {
                var hasError = false
                if (inputEmail.text.isEmpty()) {
                    hasError = true
                    inputEmail.error = "Field cannot be empty"
                }
                if (inputPassword.text.isEmpty()) {
                    hasError = true
                    inputPassword.error = "Field cannot be empty"
                }
                if (inputPasswordConfirm.text.isEmpty()) {
                    hasError = true
                    inputPasswordConfirm.error = "Field cannot be empty"
                }
                if (inputPassword.text.toString() != inputPasswordConfirm.text.toString()) {
                    hasError = true
                    inputPassword.error = "Password mismatch"
                    inputPasswordConfirm.error = "Password mismatch"
                }
                if (!hasError) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        inputEmail.text.toString(),
                        inputPassword.text.toString()
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(
                                Intent(
                                    this@AuthRegister,
                                    AuthFillBio::class.java
                                ),
                                null
                            )
                        } else {
                            Toast.makeText(
                                this@AuthRegister,
                                "Failed to register. Error: " + task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }
}
