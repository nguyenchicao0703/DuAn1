package com.fpoly.project1.firebase

import com.fpoly.project1.R
import com.fpoly.project1.activity.authentication.AuthLogin
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object Firebase {
    /**
     * Global Firebase database reference
     */
    var database: DatabaseReference = FirebaseDatabase
        .getInstance(AuthLogin.resources!!.getString(R.string.firebase_database_uri))
        .reference

    /**
     * Global Firebase storage reference
     */
    var storage: StorageReference = FirebaseStorage
        .getInstance(AuthLogin.resources!!.getString(R.string.firebase_storage_uri))
        .reference
}