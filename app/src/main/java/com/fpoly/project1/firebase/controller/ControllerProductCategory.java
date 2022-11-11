package com.fpoly.project1.firebase.controller;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.ProductCategory;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerProductCategory extends ControllerBase {
    private final String table = "table_product_categories";

    public ControllerProductCategory() {
        Firebase.database
                .child(table)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnSuccessListener(v -> Log.i("ControllerProductCategory", "Created table"));
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void setProductCategory(ProductCategory productCategory, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            productCategory.__id = Objects.requireNonNull(tableReference.getKey());
        } else {
            rowReference = tableReference.child(String.valueOf(productCategory.__id));
        }

        rowReference
                .setValue(productCategory)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void newProductCategory(ProductCategory productCategory, SuccessListener sListener, FailureListener fListener) {
        setProductCategory(productCategory, false, sListener, fListener);
    }

    public void deleteProductCategory(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getProductCategory(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getAllProductCategory(SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }
}

