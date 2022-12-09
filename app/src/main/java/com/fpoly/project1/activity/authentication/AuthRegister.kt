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
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_register)

        etEmail = findViewById(R.id.register_edt_email)
        etPassword = findViewById(R.id.register_edt_password)
        etPasswordConfirm = findViewById(R.id.register_edt_confirm_password)

        findViewById<TextView>(R.id.register_txt_signIn).setOnClickListener {
            startActivity(Intent(this@AuthRegister, AuthLogin::class.java))
            finish()
        }

        findViewById<Button>(R.id.register_btn_signUp)
            .setOnClickListener {
                var hasError = false
                if (etEmail.text.isEmpty()) {
                    hasError = true
                    etEmail.error = "Field cannot be empty"
                }
                if (etPassword.text.isEmpty()) {
                    hasError = true
                    etPassword.error = "Field cannot be empty"
                }
                if (etPasswordConfirm.text.isEmpty()) {
                    hasError = true
                    etPasswordConfirm.error = "Field cannot be empty"
                }
                if (etPassword.text.toString() != etPasswordConfirm.text.toString()) {
                    hasError = true
                    etPassword.error = "Password mismatch"
                    etPasswordConfirm.error = "Password mismatch"
                }
                if (!hasError) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        etEmail.text.toString(),
                        etPassword.text.toString()
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(
                                Intent(
                                    this@AuthRegister,
                                    AuthFillBio::class.java
                                )
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
