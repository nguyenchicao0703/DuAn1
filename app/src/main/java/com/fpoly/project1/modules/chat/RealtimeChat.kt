package com.fpoly.project1.modules.chat

import com.facebook.login.LoginManager.logOut
import com.facebook.AccessToken.isExpired
import com.facebook.Profile.id
import com.facebook.CallbackManager.Factory.create
import com.facebook.login.LoginManager.registerCallback
import com.facebook.login.LoginManager.logInWithReadPermissions
import com.facebook.CallbackManager.onActivityResult
import com.facebook.login.LoginResult.accessToken
import com.facebook.AccessToken.token
import com.facebook.GraphResponse.toString
import com.facebook.GraphRequest.parameters
import com.facebook.GraphRequest.executeAsync
import com.facebook.Profile.name
import com.facebook.Profile.getProfilePictureUri
import com.google.firebase.database.DatabaseReference
import com.fpoly.project1.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.fpoly.project1.firebase.controller.ControllerBase.SuccessListener
import com.fpoly.project1.firebase.controller.ControllerBase.FailureListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fpoly.project1.R
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.firebase.model.Product
import android.view.LayoutInflater
import com.fpoly.project1.firebase.model.ProductCategory
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.widget.TextView
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.activity.home.adapter.FeaturedAdapter
import android.widget.Toast
import android.app.Activity
import com.fpoly.project1.firebase.SessionUser
import android.content.Intent
import com.fpoly.project1.activity.account.AccountEditProfile
import com.fpoly.project1.activity.account.AccountOrderHistory
import com.fpoly.project1.activity.account.AccountFavorites
import com.google.firebase.auth.FirebaseAuth
import com.facebook.login.LoginManager
import com.fpoly.project1.activity.request_codes.RequestCode
import android.widget.EditText
import com.fpoly.project1.activity.account.adapter.FavoriteProductsAdapter
import android.text.TextWatcher
import android.text.Editable
import com.google.firebase.auth.FirebaseUser
import com.facebook.AccessToken
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.activity.authentication.AuthLoginActivity
import com.fpoly.project1.activity.greeting.IntroduceActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.facebook.CallbackManager
import androidx.core.content.ContextCompat
import com.fpoly.project1.activity.authentication.AuthRegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.FacebookException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.fpoly.project1.activity.authentication.AuthFillBioActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.GraphRequest
import com.facebook.GraphRequest.GraphJSONObjectCallback
import org.json.JSONObject
import com.facebook.GraphResponse
import org.json.JSONException
import androidx.cardview.widget.CardView
import com.fpoly.project1.activity.authentication.AuthForgotVerifyOTP
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider
import com.fpoly.project1.activity.authentication.AuthResetPassword
import com.google.firebase.auth.PhoneAuthOptions
import androidx.viewpager.widget.ViewPager
import android.annotation.SuppressLint
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fpoly.project1.activity.home.HomeFragment
import com.google.firebase.database.IgnoreExtraProperties
import com.fpoly.project1.firebase.model.ChatSession.ChatMessage
import com.fpoly.project1.firebase.controller.ControllerBase
import com.google.android.gms.tasks.Tasks
import com.fpoly.project1.firebase.model.Seller
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import java.util.HashMap

class RealtimeChat {
    private val databaseReference = Firebase.database.child("chat_sessions")

    init {
        databaseReference.get()
            .addOnCompleteListener { task: Task<DataSnapshot?> ->
                if (!task.isSuccessful) {
                    databaseReference.setValue(0)
                        .addOnCompleteListener { task2: Task<Void?> ->
                            Log.i(
                                "RealtimeChat",
                                (if (task2.isSuccessful) "Created table" else task2.exception!!.message)!!
                            )
                        }
                }
            }
    }

    fun addChatSession(
        uid1: String?,
        uid2: String?,
        sListener: SuccessListener,
        fListener: FailureListener
    ) {
        val newReference = databaseReference.push()
        databaseReference.setValue(ChatSession(newReference.key, uid1, uid2, HashMap()))
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    sListener.run()
                } else {
                    fListener.run(task.exception)
                }
            }
    }
}