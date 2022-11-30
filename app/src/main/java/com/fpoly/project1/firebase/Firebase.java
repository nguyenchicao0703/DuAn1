package com.fpoly.project1.firebase;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.authentication.AuthLoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {
    /**
     * Global Firebase database reference
     */
    public static DatabaseReference database = FirebaseDatabase
            .getInstance(AuthLoginActivity.resources.getString(R.string.firebase_database_uri))
            .getReference(); // get database reference

    /**
     * Global Firebase storage reference
     */
    public static StorageReference storage = FirebaseStorage
            .getInstance(AuthLoginActivity.resources.getString(R.string.firebase_storage_uri))
            .getReference(); // get storage reference (works the same way as database)
}
