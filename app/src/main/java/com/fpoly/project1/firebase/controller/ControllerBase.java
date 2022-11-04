package com.fpoly.project1.firebase.controller;

import com.google.firebase.database.DataSnapshot;

public abstract class ControllerBase {
    public static abstract class SuccessListener {
        public void run() {}

        public void run(Object unused) {}

        public void run(DataSnapshot dataSnapshot) {}
    }

    public static abstract class FailureListener {
        public abstract void run(Exception error);
    }
}
