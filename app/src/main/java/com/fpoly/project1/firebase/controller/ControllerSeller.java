package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Seller;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerSeller extends ControllerBase {
    private final String table = "table_sellers";
    
    public void setSeller(Seller seller, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            seller.id = Integer.parseInt(
                    Objects.requireNonNull(tableReference.getKey())
            );
        } else {
            rowReference = tableReference.child(String.valueOf(seller.id));
        }

        rowReference
                .setValue(seller)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void newSeller(Seller seller, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    Seller[] arrayList = dataSnapshot.getValue(Seller[].class);

                    if (arrayList != null && Arrays.stream(arrayList).anyMatch(c ->
                            c.emailAddress.equals(seller.emailAddress)
                    )) {
                        fListener.run(new Exception("Email taken"));
                    } else {
                        setSeller(seller, false, sListener, fListener);
                    }
                })
                .addOnFailureListener(fListener::run);
    }

    public void deleteSeller(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getSeller(int id, SuccessListener sListener, FailureListener fListener) {
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
