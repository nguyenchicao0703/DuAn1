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
            // TODO try to mix it with something instead of hard coded
        .getInstance("https://duan1-a7273-default-rtdb.asia-southeast1.firebasedatabase.app")
        .reference

    /**
     * Global Firebase storage reference
     */
    var storage: StorageReference = FirebaseStorage
        .getInstance("gs://duan1-a7273.appspot.com")
        .reference
}
