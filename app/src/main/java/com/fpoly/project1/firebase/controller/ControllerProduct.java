package com.fpoly.project1.firebase.controller;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Product;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerProduct extends ControllerBase {
    private final String table = "table_products";

    public ControllerProduct() {
        Firebase.database
                .child(table)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnSuccessListener(v -> Log.i("ControllerProduct", "Created table"));
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void setProduct(Product product, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            product.__id = Objects.requireNonNull(tableReference.getKey());
        } else {
            rowReference = tableReference.child(String.valueOf(product.__id));
        }

        rowReference
                .setValue(product)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void newProduct(Product product, SuccessListener sListener, FailureListener fListener) {
        setProduct(product, false, sListener, fListener);
    }

    public void deleteProduct(String id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getProduct(String id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getAllProduct(SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }
}

