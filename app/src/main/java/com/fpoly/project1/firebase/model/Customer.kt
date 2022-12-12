package com.fpoly.project1.firebase.model

import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.controller.ControllerBase

data class Customer(
    var id: String? = null,
    var gid: String? = null,
    var fid: String? = null,
    var avatarUrl: String? = null,
    var fullName: String? = null,
    var birthDate: String? = null,
    var emailAddress: String? = null,
    var phoneNumber: String? = null,
    var postalAddress: String? = null,
    var favoriteIds: List<String>? = null
) {
    fun getAvatarUrl(successListener: ControllerBase.SuccessListener) {
        Firebase.storage.child("/avatars/${this.id}.jpg").downloadUrl
            .addOnCompleteListener {
                if (it.isSuccessful)
                    successListener.run(it.result.toString())
                else
                    successListener.run(this.avatarUrl)
            }
    }
}
