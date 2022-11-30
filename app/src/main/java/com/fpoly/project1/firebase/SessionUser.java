package com.fpoly.project1.firebase;

import android.net.Uri;

import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.google.android.gms.tasks.Tasks;

public class SessionUser {
    public static String sessionId;
    private static final ControllerCustomer controllerCustomer = new ControllerCustomer();

    public static void setId(String id) {
        sessionId = id;
    }

    public static Uri getAvatar() {
        try {
            return Tasks.await(
                    Firebase.storage
                            .child("/avatars/" + controllerCustomer.getSync(sessionId))
                            .getDownloadUrl()
            );
        } catch (Exception e) {
            return null;
        }
    }
}
