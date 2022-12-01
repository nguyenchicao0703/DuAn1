package com.fpoly.project1.activity.account

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.project1.R
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.Customer
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class AccountEditProfile : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
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
        var acc = account
        if (acc == null) acc = controllerCustomer.getSync(SessionUser.sessionId)!!

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_edit_profile)

        // get account info
        val account = controllerCustomer.getSync(SessionUser.sessionId)!!

        // bindings
        val buttonUpdateProfile = findViewById<Button>(R.id.edit_profile_btn_save_changes)
        val buttonUpdatePassword = findViewById<Button>(R.id.edit_profile_btn_change_password)

        // edit text bindings
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

        findViewById<Button>(R.id.edit_profile_iv_back).setOnClickListener { finish() }

        // toggle update button
        for (element in arrayOf(
            profileName,
            profileEmail,
            profilePhone,
            profileAddress,
            profileBirthDate
        )) {
            element.setOnFocusChangeListener { _: View, _: Boolean ->
                when (element) {
                    profileName -> if (profileName.text.toString() == account.fullName) View.GONE else View.VISIBLE
                    profileEmail -> if (profileEmail.text.toString() == account.emailAddress) View.GONE else View.VISIBLE
                    profilePhone -> if (profilePhone.text.toString() == account.phoneNumber) View.GONE else View.VISIBLE
                    profileAddress -> if (profileAddress.text.toString() == account.postalAddress) View.GONE else View.VISIBLE
                    profileBirthDate -> if (profileBirthDate.text.toString() == account.birthDate) View.GONE else View.VISIBLE
                }
            }
        }

        // toggle edit text
        for (element in arrayOf(
            profileNameToggle,
            profileEmailToggle,
            profilePhoneToggle,
            profileAddressToggle,
            profileBirthDateToggle
        )) {
            element.setOnClickListener {
                when (element) {
                    profileNameToggle -> profileName.isEnabled = !profileName.isEnabled
                    profileEmailToggle -> profileEmail.isEnabled = !profileEmail.isEnabled
                    profilePhoneToggle -> profilePhone.isEnabled = !profilePhone.isEnabled
                    profileAddressToggle -> profileAddress.isEnabled = !profileAddress.isEnabled
                    profileBirthDateToggle -> profileBirthDate.isEnabled = !profileBirthDate.isEnabled
                }
            }
        }

        // update edit text fields
        updateFields(account)

        // update profile
        buttonUpdateProfile.setOnClickListener {
            if (controllerCustomer.setSync(
                    Customer(
                        account.__id,  // keep, reference ID
                        account.gid,  // keep
                        account.fid,  // keep
                        account.avatarUrl,  // keep
                        profileName.text.toString(),  // override
                        profileBirthDate.text.toString(),  // override
                        profileEmail.text.toString(),  // override
                        profilePhone.text.toString(),  // override
                        profileAddress.text.toString(),  // override
                        account.favoriteIds // keep
                    ), true
                )
            ) {
                Toast.makeText(this, "Successfully updated profile", Toast.LENGTH_SHORT).show()

                // update edit text fields
                updateFields(null)
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }

        // Change password button callback
        buttonUpdatePassword.setOnClickListener {
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser!!.email!!)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Reset password email has been sent, please check your inbox",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show()
                        task.exception!!.printStackTrace()
                    }
                }
        }
    }
}
