package com.fpoly.project1.activity.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import de.hdodenhof.circleimageview.CircleImageView

class AccountEditProfile : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var profileAvatar: CircleImageView
    private lateinit var profileName: EditText
    private lateinit var profileEmail: EditText
    private lateinit var profilePhone: EditText
    private lateinit var profileAddress: EditText
    private lateinit var profileBirthDate: EditText

    private lateinit var profileNameToggle: ImageView
    private lateinit var profileEmailToggle: ImageView
    private lateinit var profilePhoneToggle: ImageView
    private lateinit var profileAddressToggle: ImageView
    private lateinit var profileBirthDateToggle: ImageView

    private fun updateFields(account: Customer?) {
        controllerCustomer.getAsync(
            SessionUser.sessionId,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val acc = account ?: dataSnapshot?.getValue(Customer::class.java)!!

                    profileAvatar.let {
                        Firebase.storage.child("/avatars/${acc.id}.jpg").downloadUrl
                            .addOnCompleteListener { uri ->
                                if (uri.isSuccessful)
                                    Glide.with(this@AccountEditProfile).load(uri.result)
                                        .into(profileAvatar)
                                else
                                    Glide.with(this@AccountEditProfile).load(acc.avatarUrl)
                                        .into(profileAvatar)
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
                    profileName.let {
                        it.setText(acc.fullName)
                        it.isEnabled = false
                    }
                    profileEmail.let {
                        it.setText(acc.emailAddress)
                        it.isEnabled = false
                    }
                    profilePhone.let {
                        it.setText(acc.phoneNumber)
                        it.isEnabled = false
                    }
                    profileBirthDate.let {
                        it.setText(acc.birthDate)
                        it.isEnabled = false
                    }
                    profileAddress.let {
                        it.setText(acc.postalAddress)
                        it.isEnabled = false
                    }
                }
            },
            failureListener = null
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_profile_edit)

        // get account info
        controllerCustomer.getAsync(
            SessionUser.sessionId,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val account = dataSnapshot!!.getValue(Customer::class.java)!!

                    // bindings
                    val buttonUpdateProfile =
                        findViewById<Button>(R.id.edit_profile_btn_save_changes)
                    val buttonUpdatePassword =
                        findViewById<Button>(R.id.edit_profile_btn_change_password)

                    // edit text bindings
                    profileAvatar = findViewById(R.id.edit_profile_iv_avt)
                    profileName = findViewById(R.id.edit_profile_txt_name)
                    profileEmail = findViewById(R.id.edit_profile_txt_email)
                    profileAddress = findViewById(R.id.edit_profile_txt_address)
                    profilePhone = findViewById(R.id.edit_profile_txt_phoneNumber)
                    profileBirthDate = findViewById(R.id.edit_profile_txt_birthDate)

                    // image view bindings
                    profileNameToggle = findViewById(R.id.edit_profile_iv_edit_name)
                    profileEmailToggle = findViewById(R.id.edit_profile_iv_edit_email)
                    profileAddressToggle = findViewById(R.id.edit_profile_iv_edit_address)
                    profilePhoneToggle = findViewById(R.id.edit_profile_iv_edit_phoneNumber)
                    profileBirthDateToggle = findViewById(R.id.edit_profile_iv_edit_birthDate)

                    findViewById<ImageView>(R.id.edit_profile_iv_back).setOnClickListener { finish() }

                    // toggle update button
                    for (
                    element in arrayOf(
                        profileName,
                        profileEmail,
                        profilePhone,
                        profileAddress,
                        profileBirthDate
                    )
                    ) {
                        element.setOnFocusChangeListener { _: View, _: Boolean ->
                            when (element) {
                                profileName ->
                                    if (profileName.text.toString() == account.fullName) View.GONE else View.VISIBLE
                                profileEmail ->
                                    if (profileEmail.text.toString() == account.emailAddress) View.GONE else View.VISIBLE
                                profilePhone ->
                                    if (profilePhone.text.toString() == account.phoneNumber) View.GONE else View.VISIBLE
                                profileAddress ->
                                    if (profileAddress.text.toString() == account.postalAddress) View.GONE else View.VISIBLE
                                profileBirthDate ->
                                    if (profileBirthDate.text.toString() == account.birthDate) View.GONE else View.VISIBLE
                            }
                        }
                    }

                    // toggle edit text
                    for (
                    element in arrayOf(
                        profileNameToggle,
                        profileEmailToggle,
                        profilePhoneToggle,
                        profileAddressToggle,
                        profileBirthDateToggle
                    )
                    ) {
                        element.setOnClickListener {
                            when (element) {
                                profileNameToggle -> profileName.isEnabled = !profileName.isEnabled
                                profileEmailToggle -> profileEmail.isEnabled =
                                    !profileEmail.isEnabled
                                profilePhoneToggle -> profilePhone.isEnabled =
                                    !profilePhone.isEnabled
                                profileAddressToggle -> profileAddress.isEnabled =
                                    !profileAddress.isEnabled
                                profileBirthDateToggle -> profileBirthDate.isEnabled =
                                    !profileBirthDate.isEnabled
                            }
                        }
                    }

                    // update edit text fields
                    updateFields(account)

                    // update profile
                    buttonUpdateProfile.setOnClickListener {
                        controllerCustomer.setAsync(
                            Customer(
                                account.id, // keep, reference ID
                                account.gid, // keep
                                account.fid, // keep
                                account.avatarUrl, // keep
                                profileName.text.toString(), // override
                                profileBirthDate.text.toString(), // override
                                profileEmail.text.toString(), // override
                                profilePhone.text.toString(), // override
                                profileAddress.text.toString(), // override
                                account.favoriteIds // keep
                            ),
                            true,
                            successListener = object : ControllerBase.SuccessListener() {
                                override fun run() {
                                    Toast.makeText(
                                        this@AccountEditProfile, "Successfully updated profile",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // update edit text fields
                                    updateFields(null)
                                }
                            },
                            failureListener = object : ControllerBase.FailureListener() {
                                override fun run(error: Exception?) {
                                    Toast.makeText(
                                        this@AccountEditProfile,
                                        "Failed to update profile",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        )
                    }

                    // Change password button callback
                    buttonUpdatePassword.setOnClickListener {
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser!!.email!!)
                            .addOnCompleteListener { task: Task<Void?> ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@AccountEditProfile,
                                        "Reset password email has been sent, please check your inbox",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@AccountEditProfile, "Failed to send email", Toast
                                            .LENGTH_SHORT
                                    ).show()
                                    task.exception!!.printStackTrace()
                                }
                            }
                    }
                }
            },
            null
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RequestCode.PROFILE_IMAGE_UPLOAD || resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "User cancelled action", Toast.LENGTH_SHORT).show()
            return
        }

        data?.data?.let { this.contentResolver.openInputStream(it) }?.let {
            Firebase.storage.child("/avatars/${SessionUser.sessionId}.jpg").putStream(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Updated avatar", Toast.LENGTH_SHORT)
                            .show()

                        SessionUser.avatar.addOnCompleteListener { uri ->
                            Glide.with(this).load(uri.result)
                                .into(findViewById(R.id.edit_profile_iv_avt))
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to upload avatar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}
