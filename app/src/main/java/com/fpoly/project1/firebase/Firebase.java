package com.fpoly.project1.firebase;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.authentication.AuthLoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {
    /**
     * Global Firebase database reference
     */
    public static DatabaseReference database = FirebaseDatabase
            // TODO: replace with AndroidManifest or encrypt this, somehow
            .getInstance(AuthLoginActivity.resources.getString(R.string.firebase_database_uri))
            .getReference(); // get database reference
    private static String sessionId;

    /**
     * Get current session account Customer ID
     *
     * @return Firebase reference ID
     */
    public static String getSessionId() {
        return sessionId;
    }

    /**
     * Set current session account Customer ID
     *
     * @param __id Firebase reference ID to the Customer object
     */
    public static void setSessionId(String __id) {
        sessionId = __id;
    }
}
