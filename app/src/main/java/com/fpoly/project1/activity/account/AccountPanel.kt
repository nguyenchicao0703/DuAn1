package com.fpoly.project1.activity.account

import android.content.Intent
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.fpoly.project1.R
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    override fun onResume() {
        super.onResume()

        // back button?
        backButton = requireActivity().findViewById(R.id.profile_iv_back)
        backButton.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0)
                parentFragmentManager.popBackStack()
        }

        // set avatar
        profileAvatar = requireActivity().findViewById<ImageView>(R.id.profile_iv_avt)
        profileAvatar.let {
            SessionUser.avatar.addOnCompleteListener { uri ->
                if (uri.isSuccessful)
                    Glide.with(requireContext()).load(uri.result)
                        .into(requireActivity().findViewById(R.id.profile_iv_avt))
                else
                    ControllerCustomer().getAsync(
                        SessionUser.sessionId,
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                val customer = dataSnapshot?.getValue(Customer::class.java)!!

                                Glide.with(requireContext()).load(customer.avatarUrl)
                                    .into(requireActivity().findViewById(R.id.profile_iv_avt))
                            }
                        },
                        failureListener = null
                    )
            }
        }

        // view information activity
        profileInfoButton = requireActivity().findViewById(R.id.profile_btn_use_information)
        profileInfoButton.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    AccountEditProfile::class.java
                )
            )
        }

        // chat activity
        profileChatButton = requireActivity().findViewById(R.id.profile_btn_order_chat)
        profileChatButton.setOnClickListener {
            requireActivity().let {
                it.findViewById<ViewPager>(R.id.viewPager)
                    .currentItem = 3
                it.findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
                    .selectedItemId = R.id.mChat
            }
        }

        // order history activity
        profileOrdersButton = requireActivity().findViewById(R.id.profile_btn_order_history)
        profileOrdersButton.text = "(Missing layout)"
        /**.setOnClickListener {
        startActivity(
        Intent(
        context,
        AccountOrderHistory::class.java
        )
        )
        }*/

        // favorite products activity
        profileFavoriteButton = requireActivity().findViewById(R.id.profile_btn_favourite)
        profileFavoriteButton.setOnClickListener {
            startActivity(Intent(context, AccountFavorites::class.java))
        }

        // session logout
        profileLogoutButton = requireActivity().findViewById(R.id.profile_btn_logOut)
        profileLogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            requireActivity().finishActivity(RequestCode.LOGOUT)
            requireActivity().finish()
        }
    }
}
