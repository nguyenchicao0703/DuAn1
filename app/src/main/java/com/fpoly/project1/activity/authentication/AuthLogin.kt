package com.fpoly.project1.activity.authentication

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fpoly.project1.R
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.activity.request_codes.RequestCode
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.json.JSONObject

class AuthLogin : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for use by Firebase
        Companion.resources = resources

        // proceed with the activity
        setContentView(R.layout.activity_login)
        val window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        // switching between login and signup btn
        findViewById<TextView>(R.id.auth_tv_switch).setOnClickListener {
            startActivity(Intent(this, AuthRegister::class.java))
            finish()
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
            startActivityForResult(googleSignInClient.signInIntent, RequestCode.GOOGLE_SIGNIN)
        }

        // login via email and password
        val etEmail = findViewById<EditText>(R.id.auth_et_email)
        val etPass = findViewById<EditText>(R.id.auth_et_password)

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
                LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
            }
    }

    // Listen for activity result from Google auth popup and Facebook auth screen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // facebook auth
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // google auth
        if (requestCode != RequestCode.GOOGLE_SIGNIN) return
        try {
            val googleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(
                ApiException::class.java
            )
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
                .addOnCompleteListener { task -> googleCompleteListener(task) }
        } catch (apiException: ApiException) {
            Toast.makeText(this@AuthLogin, "Something went wrong", Toast.LENGTH_SHORT)
                .show()
            apiException.printStackTrace()
        }
    }

    // Google auth complete listener
    private fun googleCompleteListener(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val customers = controllerCustomer.getAllSync()
            if (customers == null) {
                Toast.makeText(this, "Failed to get users from database", Toast.LENGTH_SHORT).show()
                return
            }

            // we will log user in via firebase since it include both
            // password-based auth and google auth,
            // here we're just creating a customer object linked to
            // the firebase account
            val user = FirebaseAuth.getInstance().currentUser!!

            // get matching account
            val matchingCustomer = customers.stream()
                .filter { c: Customer -> c.emailAddress == user.email || c.gid == user.uid }
                .toArray()

            // if there isn't any matching user, switch to fill bio activity
            if (matchingCustomer.isEmpty()) {
                startActivity(Intent(this, AuthFillBio::class.java))
            } else {
                // if user is already exist
                Log.i("LoginActivity::Google", "Got account from Firebase")
                SessionUser.setId((matchingCustomer[0] as Customer).__id)
                startActivity(Intent(this@AuthLogin, MainActivity::class.java))
            }
        } else {
            task.exception?.printStackTrace()
            Toast.makeText(
                this,
                "Auth failed: " + task.exception?.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Facebook auth complete listener
    private fun facebookCompleteListener(loginResult: LoginResult) {
        // Authenticate with firebase
        val authCredential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        firebaseAuth.signInWithCredential(authCredential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val customers = controllerCustomer.getAllSync()

                    if (customers == null) {
                        Toast.makeText(
                            this,
                            "Failed to get users from database",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnCompleteListener
                    }

                    // TODO test and see if Facebook's Firebase user has the necessary credentials or not and remove GraphRequest code below
                    // Get facebook related information
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { jsonObject: JSONObject?, graphResponse: GraphResponse? ->
                        Log.i("LoginActivity::Facebook", graphResponse.toString())
                        try {
                            val profile: Profile = Profile.getCurrentProfile()!!
                            val email = jsonObject!!.getString("email")
                            val matchingCustomer = customers.stream()
                                .filter { c: Customer -> c.emailAddress == email || c.fid == profile.id }
                                .toArray()

                            if (matchingCustomer.isEmpty()) {
                                startActivity(Intent(this, AuthFillBio::class.java))
                            } else {
                                // if user is already exist
                                Log.i("LoginActivity::Facebook", "Got account from Firebase")
                                SessionUser.setId((matchingCustomer[0] as Customer).__id)
                                startActivity(
                                    Intent(
                                        this@AuthLogin,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@AuthLogin,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                    }

                    val bundle = Bundle()
                    bundle.putString("fields", "id,name,email")
                    request.parameters = bundle
                    request.executeAsync()
                } else {
                    Toast.makeText(
                        this@AuthLogin,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                    task.exception!!.printStackTrace()
                }
            }
    }

    companion object {
        var resources: Resources? = null
    }
}