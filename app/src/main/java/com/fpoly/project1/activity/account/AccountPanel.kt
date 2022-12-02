package com.fpoly.project1.activity.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatSelector
import com.fpoly.project1.activity.request_codes.RequestCode
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

                        Glide.with(requireContext()).load(SessionUser.avatar)
                            .into(requireActivity().findViewById(R.id.profile_iv_avt))
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity

        // back button?
        activity.findViewById<ImageView>(R.id.profile_iv_back)
            .setOnClickListener { TODO("Add back function, if any") }

        // set avatar
        activity.findViewById<ImageView>(R.id.profile_iv_avt).let {
            Glide.with(context).load(SessionUser.avatar).into(it)
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
        activity.findViewById<View>(R.id.profile_cardView_use_information)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        AccountEditProfile::class.java
                    )
                )
            }

        // chat activity
        activity.findViewById<View>(R.id.profile_cardView_chat)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        ChatSelector::class.java
                    )
                )
            }

        // order history activity
        activity.findViewById<View>(R.id.profile_cardView_order_history)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        AccountOrderHistory::class.java
                    )
                )
            }

        // favorite products activity
        activity.findViewById<View>(R.id.profile_cardView_favourite)
            .setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        AccountFavorites::class.java
                    )
                )
            }

        // session logout
        activity.findViewById<View>(R.id.profile_cardView_logOut)
            .setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                activity.finishActivity(RequestCode.LOGOUT)
                activity.finish()
            }
    }
}