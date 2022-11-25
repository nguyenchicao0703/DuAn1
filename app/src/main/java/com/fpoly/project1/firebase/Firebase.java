package com.fpoly.project1.firebase;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.AuthActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {
    public static DatabaseReference database = FirebaseDatabase
            // TODO: replace with AndroidManifest or encrypt this, somehow
            .getInstance(AuthActivity.resources.getString(R.string.firebase_database_uri))
            .getReference(); // get database reference
}
