package com.fpoly.project1.firebase

import android.net.Uri
import com.google.android.gms.tasks.Tasks

object SessionUser {
    var sessionId: String? = null
    val cart: MutableList<Pair<String, Int>> = ArrayList()

    fun setId(id: String?) {
        sessionId = id
    }

    val avatar: Uri?
        get() = try {
            Tasks.await(
                Firebase.storage
                    .child("/avatars/$sessionId.jpg")
                    .downloadUrl
            )
        } catch (_: Throwable) {
            null
        }
}
