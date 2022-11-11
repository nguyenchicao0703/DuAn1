package com.fpoly.project1.firebase.controller;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Seller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ControllerSeller extends ControllerBase {
    private final String table = "table_sellers";

    public ControllerSeller() {
        Firebase.database
                .child(table)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnSuccessListener(v -> Log.i("ControllerSeller", "Created table"));
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void setSeller(Seller seller, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            seller.__id = Objects.requireNonNull(tableReference.getKey());

            Firebase.database
                    .child(this.table)
                    .get()
                    .addOnSuccessListener(dataSnapshot -> {
                        List<Seller> arrayList = new ArrayList<>();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            arrayList.add(ds.getValue(Seller.class));
                        }

                        if (arrayList.stream().anyMatch(c ->
                                c.emailAddress.equals(seller.emailAddress)
                        )) {
                            fListener.run(new Exception("Email taken"));
                        } else {
                            setSeller(seller, false, sListener, fListener);

                            rowReference
                                    .setValue(seller)
                                    .addOnSuccessListener(sListener::run)
                                    .addOnFailureListener(fListener::run);
                        }
                    })
                    .addOnFailureListener(fListener::run);
        } else {
            rowReference = tableReference.child(String.valueOf(seller.__id));

            rowReference
                    .setValue(seller)
                    .addOnSuccessListener(sListener::run)
                    .addOnFailureListener(fListener::run);
        }
    }

    public void newSeller(Seller seller, SuccessListener sListener, FailureListener fListener) {
        setSeller(seller, false, sListener, fListener);
    }

    public void deleteSeller(String id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getSeller(String id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getAllSeller(SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }
}
