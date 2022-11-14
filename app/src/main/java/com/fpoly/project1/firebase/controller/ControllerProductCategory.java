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
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        task.getException().printStackTrace();
                    else if (task.getResult().getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnSuccessListener(v -> Log.i("ControllerProductCategory", "Created table"));
                    }
                });
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run();
                    else
                        fListener.run(task.getException());
                });
    }

    public void newProductCategory(ProductCategory productCategory, SuccessListener sListener, FailureListener fListener) {
        setProductCategory(productCategory, false, sListener, fListener);
    }

    public void deleteProductCategory(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run(task);
                    else
                        fListener.run(task.getException());
                });
    }

    public void getProductCategory(int id, SuccessListener sListener, FailureListener fListener) {
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

    public void getAllProductCategory(SuccessListener sListener, FailureListener fListener) {
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

