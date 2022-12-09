package com.fpoly.project1.firebase

import android.net.Uri
import com.fpoly.project1.firebase.model.Product
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

object SessionUser {
    var sessionId: String? = null
    val cart: MutableList<Pair<Product, Int>> = ArrayList()

    fun setId(id: String?) {
        sessionId = id
    }

    val avatar: Task<Uri>
        get() =
            Firebase.storage
                .child("/avatars/$sessionId.jpg")
                .downloadUrl
}
