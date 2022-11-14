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
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        task.getException().printStackTrace();
                    else if (task.getResult().getValue() == null) {
                        Firebase.database
                                .child(table)
                                .setValue(0)
                                .addOnSuccessListener(v -> Log.i("ControllerSeller", "Created table"));
                    }
                });
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
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            fListener.run(task.getException());
                            return;
                        }

                        List<Seller> arrayList = new ArrayList<>();

                        for (DataSnapshot ds : task.getResult().getChildren()) {
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
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful())
                                            sListener.run();
                                        else
                                            fListener.run(task2.getException());
                                    });
                        }
                    });
        } else {
            rowReference = tableReference.child(String.valueOf(seller.__id));

            rowReference
                    .setValue(seller)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            sListener.run();
                        else
                            fListener.run(task.getException());
                    });
        }
    }

    public void deleteSeller(String id, SuccessListener sListener, FailureListener fListener) {
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

    public void getSeller(String id, SuccessListener sListener, FailureListener fListener) {
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

    public void getAllSeller(SuccessListener sListener, FailureListener fListener) {
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
