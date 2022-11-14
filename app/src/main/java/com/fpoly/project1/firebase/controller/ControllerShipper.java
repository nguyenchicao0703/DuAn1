package com.fpoly.project1.firebase.controller;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Shipper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerShipper extends ControllerBase {
    private final String table = "table_shippers";

    public ControllerShipper() {
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
                                .addOnSuccessListener(v -> Log.i("ControllerShipper", "Created table"));
                    }
                });
    }

    public void setShipper(Shipper shipper, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            shipper.__id = Objects.requireNonNull(tableReference.getKey());

            Firebase.database
                    .child(this.table)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            fListener.run(task.getException());
                            return;
                        }

                        List<Shipper> arrayList = new ArrayList<>();

                        for (DataSnapshot ds : task.getResult().getChildren())
                            arrayList.add(ds.getValue(Shipper.class));

                        if (arrayList.stream().anyMatch(c ->
                                c.emailAddress.equals(shipper.emailAddress) && c.licenseNumber.equals(shipper.licenseNumber)
                        )) {
                            fListener.run(new Exception("Email or License is already been taken"));
                        } else {
                            rowReference
                                    .setValue(shipper)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful())
                                            sListener.run();
                                        else
                                            fListener.run(task2.getException());
                                    });
                        }
                    });
        } else {
            rowReference = tableReference.child(String.valueOf(shipper.__id));

            rowReference
                    .setValue(shipper)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            sListener.run();
                        else
                            fListener.run(task.getException());
                    });
        }
    }

    public void deleteShipper(String id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(id)
                .setValue(null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run();
                    else
                        fListener.run(task.getException());
                });
    }

    public void getShipper(String id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        sListener.run(task.getResult());
                    else
                        fListener.run(task.getException());
                });
    }

    public void getAllShipper(SuccessListener sListener, FailureListener fListener) {
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
