package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Customer;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Objects;

public class ControllerCustomer extends ControllerBase {
    // table to use
    private final String table = "table_customers";

    /**
     * Broadcast object:
     * {
     * success: boolean, <-- response status, true if success, else false
     * value: any        <-- result, if any
     * error: String     <-- error message if failed
     * }
     * <p>
     * Firebase Realtime Database document
     * Please read: https://firebase.google.com/docs/database/android/read-and-write
     */

    public void setCustomer(Customer customer, boolean update, SuccessListener sListener, FailureListener fListener) {
        DatabaseReference tableReference = Firebase.database // database reference
                .child(this.table); // get the table reference
        DatabaseReference rowReference;

        // if add new customer
        if (!update) {
            // set as new entry location reference (basically new unique ID)
            rowReference = tableReference.push();

            // override customer unique ID
            customer.id = Integer.parseInt(
                    Objects.requireNonNull(tableReference.getKey())
            );
        } else {
            // set as existing location reference
            rowReference = tableReference.child(String.valueOf(customer.id));
        }

        rowReference
                .setValue(customer) // set value to new location
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void newCustomer(Customer customer, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get() // read all entries from table (or row/json tree)
                .addOnSuccessListener(dataSnapshot -> {
                    // get result as arraylist
                    Customer[] arrayList = dataSnapshot.getValue(Customer[].class);

                    // check if any account with matching email exist
                    if (arrayList != null && Arrays.stream(arrayList).anyMatch(c ->
                            c.emailAddress.equals(customer.emailAddress)
                    )) {
                        fListener.run(new Exception("Email taken"));
                    } else {
                        setCustomer(customer, false, sListener, fListener);
                    }
                })
                .addOnFailureListener(fListener::run);
    }

    public void deleteCustomer(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .setValue(null) // set null to delete entry
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getCustomer(int id, SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .child(String.valueOf(id))
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }

    public void getAllCustomer(SuccessListener sListener, FailureListener fListener) {
        Firebase.database
                .child(this.table)
                .get()
                .addOnSuccessListener(sListener::run)
                .addOnFailureListener(fListener::run);
    }
}
