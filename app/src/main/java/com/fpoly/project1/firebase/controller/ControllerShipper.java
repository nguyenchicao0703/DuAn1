package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Shipper;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerShipper extends ControllerBase {
    private final String table = "table_shippers";

    public void setShipper(Shipper shipper, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database
                .child(this.table);
        DatabaseReference rowReference;

        if (!update) {
            rowReference = tableReference.push();

            shipper.id = Integer.parseInt(
                    Objects.requireNonNull(tableReference.getKey())
            );
        } else {
            rowReference = tableReference.child(String.valueOf(shipper.id));
        }

        rowReference
                .setValue(shipper)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void newShipper(Shipper shipper, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    Shipper[] arrayList = dataSnapshot.getValue(Shipper[].class);

                    if (arrayList != null && Arrays.stream(arrayList).anyMatch(c ->
                            c.emailAddress.equals(shipper.emailAddress) && c.licenseNumber.equals(shipper.licenseNumber)
                    )) {
                        fListener.run(new Exception("Email or License taken"));
                    } else {
                        setShipper(shipper, false, sListener, fListener);
                    }
                })
                .addOnFailureListener(fListener::run);
    }

    public void deleteShipper(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null)
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getShipper(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getAllShipper(SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }
}
