package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.ProductCategory;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerProductCategory extends ControllerBase {
    private final String table = "table_product_categories";

    public void setProduct(ProductCategory productCategory, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            productCategory.id = Integer.parseInt(
                    Objects.requireNonNull(tableReference.getKey())
            );
        } else {
            rowReference = tableReference.child(String.valueOf(productCategory.id));
        }

        rowReference
                .setValue(productCategory)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void newProduct(ProductCategory productCategory, SuccessListener sListener, FailureListener fListener) {
        setProduct(productCategory, false, sListener, fListener);
    }

    public void deleteProduct(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getProduct(int id, SuccessListener sListener, FailureListener fListener) {
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

