package com.fpoly.project1.firebase.controller;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Rating;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerRating extends ControllerBase {
    private final String table = "table_ratings";

    public ControllerRating() {
        Firebase.database
                .child(table)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        task.getException().printStackTrace();
                    else if (task.getResult().getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnSuccessListener(v -> Log.i("ControllerRating", "Created table"));
                    }
                });
    }

    public void setRating(Rating rating, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            rating.__id = Objects.requireNonNull(tableReference.getKey());
        } else {
            rowReference = tableReference.child(String.valueOf(rating.__id));
        }

        rowReference
                .setValue(rating)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run();
                    else
                        fListener.run(task.getException());
                });
    }

    public void newRating(Rating rating, SuccessListener sListener, FailureListener fListener) {
        setRating(rating, false, sListener, fListener);
    }

    public void deleteRating(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run();
                    else
                        fListener.run(task.getException());
                });
    }

    public void getRating(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run(task.getResult());
                    else
                        fListener.run(task.getException());
                });
    }

    public void getAllRating(SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run(task.getResult());
                    else
                        fListener.run(task.getException());
                });
    }
}
