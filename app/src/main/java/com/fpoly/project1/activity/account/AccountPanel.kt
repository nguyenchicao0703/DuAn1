package com.fpoly.project1.activity.account

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatSelector
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.google.firebase.auth.FirebaseAuth

class AccountPanel : Fragment(R.layout.profile_overview) {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RequestCode.PROFILE_IMAGE_UPLOAD || resultCode != Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "User cancelled action", Toast.LENGTH_SHORT).show()
            return
        }

        data?.data?.let { requireContext().contentResolver.openInputStream(it) }?.let {
            Firebase.storage.child("/avatars/${SessionUser.sessionId}.jpg").putStream(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Updated avatar", Toast.LENGTH_SHORT)
                            .show()

                        SessionUser.avatar.addOnCompleteListener { uri ->
                            Glide.with(requireContext()).load(uri.result)
                                .into(requireActivity().findViewById(R.id.profile_iv_avt))
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to upload avatar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()

        // back button?
        requireActivity().findViewById<ImageView>(R.id.profile_iv_back)
            .setOnClickListener {
                if (parentFragmentManager.backStackEntryCount > 0)
                    parentFragmentManager.popBackStack()
            }

        // set avatar
        requireActivity().findViewById<ImageView>(R.id.profile_iv_avt).let {
            SessionUser.avatar.addOnCompleteListener { uri ->
                Glide.with(requireContext()).load(uri.result)
                    .into(requireActivity().findViewById(R.id.profile_iv_avt))
            }

            it.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    RequestCode.PROFILE_IMAGE_UPLOAD
                )
            }
        }

        // view information activity
        requireActivity().findViewById<AppCompatButton>(R.id.profile_btn_use_information)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        AccountEditProfile::class.java
                    )
                )
            }

        // chat activity
        requireActivity().findViewById<AppCompatButton>(R.id.profile_btn_order_chat)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        ChatSelector::class.java
                    )
                )
            }

        // order history activity
        requireActivity().findViewById<AppCompatButton>(R.id.profile_btn_order_history)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        AccountOrderHistory::class.java
                    )
                )
            }

        // favorite products activity
        requireActivity().findViewById<AppCompatButton>(R.id.profile_btn_favourite)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        AccountFavorites::class.java
                    )
                )
            }

        // session logout
        requireActivity().findViewById<AppCompatButton>(R.id.profile_btn_logOut)
            .setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                requireActivity().finishActivity(RequestCode.LOGOUT)
                requireActivity().finish()
            }
    }
}
