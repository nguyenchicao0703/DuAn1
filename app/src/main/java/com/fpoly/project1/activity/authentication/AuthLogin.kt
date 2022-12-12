package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fpoly.project1.R
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import org.json.JSONException
import org.json.JSONObject

class AuthLogin : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_login)

        // login via email and password
        val etEmail = findViewById<EditText>(R.id.auth_et_email)
        val etPass = findViewById<EditText>(R.id.auth_et_password)

        // proceed with the activity
        val window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        // switching between login and signup btn
        findViewById<TextView>(R.id.auth_tv_switch).setOnClickListener {
            startActivity(Intent(this, AuthRegister::class.java))
            finish()
        }

        // forgot password
        findViewById<TextView>(R.id.auth_tv_forgot).setOnClickListener {
            if (etEmail.text.toString().isEmpty()) {
                etEmail.error = "This field is required"
                return@setOnClickListener
            }

            val bundle = Bundle()
            bundle.putString("email", etEmail.text.toString())

            val intent = Intent(this@AuthLogin, AuthForgotPassword::class.java)
            intent.putExtras(bundle)

            startActivity(intent)
        }

        // ============================ GOOGLE AUTHENTICATION ============================
        // firebase related
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
        )

        // login via google button
        findViewById<Button>(R.id.auth_btn_google).setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RequestCode.GOOGLE_SIGN_IN)
        }

        findViewById<Button>(R.id.auth_btn_submit)
            .setOnClickListener {
                var hasError = false
                if (etEmail.text.toString().isEmpty()) {
                    etEmail.error = "Email must not be empty"
                    hasError = true
                }
                if (etPass.text.toString().isEmpty()) {
                    etPass.error = "Password must not be empty"
                    hasError = true
                }
                if (!hasError) {
                    // sign in with email and password
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        etEmail.text.toString(),
                        etPass.text.toString()
                    ).addOnCompleteListener { task -> googleCompleteListener(task) }
                }
            }

        // ============================ FACEBOOK AUTHENTICATION ============================
        // facebook CallbackManager Factory
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    facebookCompleteListener(result)
                }

                override fun onCancel() {
                    Toast.makeText(
                        this@AuthLogin,
                        "User cancelled action",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        this@AuthLogin,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                    error.printStackTrace()
                }
            })

        // login via facebook button
        findViewById<Button>(R.id.auth_btn_facebook)
            .setOnClickListener {
                LoginManager.getInstance()
                    .logInWithReadPermissions(this, listOf("public_profile", "email"))
            }
    }

    // Listen for activity result from Google auth popup and Facebook auth screen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // facebook auth
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // google auth
        if (requestCode != RequestCode.GOOGLE_SIGN_IN) return
        try {
            val googleSignInAccount =
                GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
            if (googleSignInAccount == null) {
                Toast.makeText(this, "Received Null while getting account", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            firebaseAuth
                .signInWithCredential(
                    GoogleAuthProvider.getCredential(
                        googleSignInAccount.idToken,
                        null
                    )
                )
                .addOnCompleteListener { googleCompleteListener(it) }
        } catch (apiException: ApiException) {
            Toast.makeText(this@AuthLogin, "Something went wrong", Toast.LENGTH_SHORT)
                .show()
            Log.e(this@AuthLogin::class.simpleName, "Error while getting results", apiException)
        }
    }

    // Google auth complete listener
    private fun googleCompleteListener(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // get user
            val user = FirebaseAuth.getInstance().currentUser!!

            Firebase.database.child(controllerCustomer.table)
                .orderByChild("emailAddress").equalTo(user.email)
                .get().addOnCompleteListener { dataSnapshotTask ->
                    val matchingCustomer = dataSnapshotTask.result?.children?.toList()

                    if (!dataSnapshotTask.isSuccessful || matchingCustomer == null) {
                        startActivity(Intent(this@AuthLogin, AuthFillBio::class.java))

                        return@addOnCompleteListener
                    }

                    matchingCustomer[0].let {
                        SessionUser.setId((it.getValue(Customer::class.java))!!.id)
                        startActivity(Intent(this@AuthLogin, MainActivity::class.java))
                    }
                }
        } else {
            Log.e(this@AuthLogin::class.simpleName, "Error while authenticating", task.exception)
            Toast.makeText(
                this,
                "Auth failed: " + task.exception?.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Facebook auth complete listener
    private fun facebookCompleteListener(loginResult: LoginResult) {
        // Get facebook related information
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { jsonObject: JSONObject?, _: GraphResponse? ->
            try {
                val email = jsonObject!!.getString("email")

                Firebase.database.child(controllerCustomer.table)
                    .orderByChild("emailAddress").equalTo(email)
                    .get().addOnCompleteListener { dataSnapshotTask ->
                        val matchingCustomer = dataSnapshotTask.result?.children?.toList()

                        if (!dataSnapshotTask.isSuccessful || matchingCustomer == null) {
                            startActivity(Intent(this@AuthLogin, AuthFillBio::class.java))

                            return@addOnCompleteListener
                        }

                        matchingCustomer[0].let {
                            SessionUser.setId((it.getValue(Customer::class.java))!!.id)
                            startActivity(Intent(this@AuthLogin, MainActivity::class.java))
                        }
                    }
            } catch (e: JSONException) {
                Toast.makeText(
                    this@AuthLogin,
                    "Something went wrong. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                Log.e(this@AuthLogin::class.simpleName, "Error", e)
            }
        }

        request.parameters = bundleOf(Pair("fields", "id,name,email"))
        request.executeAsync()
    }
}