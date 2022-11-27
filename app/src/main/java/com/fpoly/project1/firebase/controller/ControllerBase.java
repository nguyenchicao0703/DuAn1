package com.fpoly.project1.firebase.controller;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public abstract class ControllerBase<T> {
    protected final String table;

    public ControllerBase(String table) {
        this.table = table;

        Firebase.database
                .child(table)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnCompleteListener(task2 ->
                                        Log.i("Controller - " + this.table, task2.isSuccessful() ? "Created table" : task2.getException().getMessage())
                                );
                    } else {
                        if (task.getException() != null)
                            task.getException().printStackTrace();
                    }
                });
    }

    // Set new value or update existing one
    public abstract boolean setSync(T value, boolean update);

    public abstract void setAsync(T value, boolean update, SuccessListener successListener, FailureListener failureListener);

    // Remove value at index
    public abstract boolean removeSync(String referenceId);

    public abstract void removeAsync(String referenceId, SuccessListener successListener, FailureListener failureListener);

    // Get value at index
    public abstract T getSync(String referenceId);

    public abstract void getAsync(String referenceId, SuccessListener successListener, FailureListener failureListener);

    // Get all values from table
    public abstract ArrayList<T> getAllSync();

    public abstract void getAllAsync(SuccessListener successListener, FailureListener failureListener);

    // Success listener
    public static abstract class SuccessListener {
        public void run() {
        }

        public void run(Object unused) {
        }

        public void run(DataSnapshot dataSnapshot) {
        }
    }

    // Failure listener
    public static abstract class FailureListener {
        public abstract void run(Exception error);
    }
}
