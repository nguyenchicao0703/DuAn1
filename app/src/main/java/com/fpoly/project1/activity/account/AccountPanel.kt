package com.fpoly.project1.activity.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.fpoly.project1.R
import com.fpoly.project1.activity.MenuID
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

class AccountPanel : Fragment(R.layout.profile_overview) {
    private lateinit var backButton: ImageView
    private lateinit var profileAvatar: ImageView
    private lateinit var profileInfoButton: AppCompatButton
    private lateinit var profileChatButton: AppCompatButton
    private lateinit var profileOrdersButton: AppCompatButton
    private lateinit var profileFavoriteButton: AppCompatButton
    private lateinit var profileLogoutButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_overview, container, false)

        // set avatar
        profileAvatar = view.findViewById(R.id.profile_iv_avt)

        // view information activity
        profileInfoButton = view.findViewById(R.id.profile_btn_use_information)
        profileInfoButton.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    AccountEditProfile::class.java
                )
            )
        }

        // chat activity
        profileChatButton = view.findViewById(R.id.profile_btn_order_chat)
        profileChatButton.setOnClickListener {
            requireActivity().findViewById<ViewPager>(R.id.viewPager)
                .currentItem = MenuID.Chat
        }

        // order history activity
        profileOrdersButton = view.findViewById(R.id.profile_btn_order_history)
        profileOrdersButton.setOnClickListener {
            AccountOrderHistory().show(parentFragmentManager, "account_order_history")
        }

        // favorite products activity
        profileFavoriteButton = view.findViewById(R.id.profile_btn_favourite)
        profileFavoriteButton.setOnClickListener {
            AccountFavorites().show(parentFragmentManager, "account_favorites")
        }

        // session logout
        profileLogoutButton = view.findViewById(R.id.profile_btn_logOut)
        profileLogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            requireActivity().finishActivity(RequestCode.LOGOUT)
            requireActivity().finish()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        SessionUser.avatar.addOnCompleteListener { uri ->
            if (uri.isSuccessful)
                Glide.with(requireContext()).load(uri.result).into(profileAvatar)
            else
                ControllerCustomer().getAsync(
                    SessionUser.sessionId,
                    successListener = object : ControllerBase.SuccessListener() {
                        override fun run(dataSnapshot: DataSnapshot?) {
                            val customer = dataSnapshot?.getValue(Customer::class.java)!!

                            Glide.with(requireContext()).load(customer.avatarUrl)
                                .into(profileAvatar)
                        }
                    },
                    failureListener = null
                )
        }
    }
}
