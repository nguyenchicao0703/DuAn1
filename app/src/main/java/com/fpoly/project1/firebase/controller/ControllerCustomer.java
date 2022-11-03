package com.fpoly.project1.firebase.controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Customer;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ControllerCustomer extends Service {
    /**
     * Broadcast object:
     * {
     *     success: boolean, <-- response status, true if success, else false
     *     error: String     <-- error message if failed
     * }
     */

    public ControllerCustomer() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cheetah.service.customer.get");
        intentFilter.addAction("cheetah.service.customer.getAll");
        intentFilter.addAction("cheetah.service.customer.new");
        intentFilter.addAction("cheetah.service.customer.set");
        intentFilter.addAction("cheetah.service.customer.delete");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    // get single customer
                    case "cheetah.service.customer.get": {
                        if (intent.getExtras() == null)
                            return;

                        getCustomer(intent.getExtras().getInt("value"));
                        break;
                    }

                    // get all customer entries
                    case "cheetah.service.customer.getAll": {
                        getAllCustomer();
                        break;
                    }

                    // insert new customer entry
                    case "cheetah.service.customer.new": {
                        if (intent.getExtras() == null)
                            return;

                        newCustomer((Customer) intent.getExtras().getSerializable("value"));
                        break;
                    }

                    // override old customer entry
                    case "cheetah.service.customer.set": {
                        if (intent.getExtras() == null)
                            return;

                        setCustomer((Customer) intent.getExtras().getSerializable("value"), true);
                        break;
                    }

                    // delete old customer entry
                    case "cheetah.service.customer.delete": {
                        if (intent.getExtras() == null)
                            return;

                        deleteCustomer(intent.getExtras().getInt("value"));
                        break;
                    }
                    default: {
                        // todo
                    }
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *
     * Firebase Realtime Database document
     * Please read: https://firebase.google.com/docs/database/android/read-and-write
     *
     */

    public void setCustomer(Customer customer, boolean update) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        DatabaseReference tableReference = Firebase.database // database reference
                .child("table_customers"); // get the table reference
        DatabaseReference rowReference;

        // if add new customer
        if (!update) {
            rowReference = tableReference.push(); // set as new entry location reference (basically new unique ID)

            customer.id = Integer.parseInt(
                    Objects.requireNonNull(tableReference.getKey())
            ); // override customer unique ID

            intent.setAction("cheetah.service.customer.new.result"); // broadcast
        } else {
            rowReference = tableReference.child(String.valueOf(customer.id)); // set as existing location reference

            intent.setAction("cheetah.service.customer.set.result"); // broadcast
        }

        rowReference
                .setValue(customer) // set value to new location
                .addOnSuccessListener(unused -> { // success listener
                    bundle.putBoolean("success", true);
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                })
                .addOnFailureListener(err -> { // failed listener
                    bundle.putBoolean("success", false);
                    bundle.putString("error", err.getMessage());
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                });
    }

    public void newCustomer(Customer customer) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        intent.setAction("cheetah.service.customer.new.result");

        Firebase.database
                .child("table_customers")
                .get() // read all entries from table (or row/json tree)
                .addOnSuccessListener(dataSnapshot -> {
                    ArrayList<Customer> arrayList = dataSnapshot.getValue(ArrayList.class); // get result as arraylist

                    assert arrayList != null;
                    Optional<Customer> any = arrayList.stream().filter(c ->
                            c.emailAddress.equals(customer.emailAddress)
                    ).findAny(); // check if any account with matching email exist

                    if (any.isPresent()) {
                        bundle.putBoolean("success", false);
                        bundle.putString("error", "Email taken");
                        intent.putExtras(bundle);

                        sendBroadcast(intent);
                    } else {
                        setCustomer(customer, false); // proceed to insert new entry
                    }
                })
                .addOnFailureListener(err -> {
                    bundle.putBoolean("success", false);
                    bundle.putString("error", err.getMessage());
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                });
    }

    public void deleteCustomer(int id) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.setAction("cheetah.service.customer.delete.result");

        Firebase.database
                .child("table_customers")
                .child(String.valueOf(id))
                .setValue(null) // set null to delete entry
                .addOnSuccessListener(dataSnapshot -> {
                    bundle.putBoolean("success", true);
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                })
                .addOnFailureListener(err -> {
                    bundle.putBoolean("success", false);
                    bundle.putString("error", err.getMessage());
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                });
    }

    public void getCustomer(int id) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.setAction("cheetah.service.customer.get.result");

        Firebase.database
                .child("table_customers")
                .child(String.valueOf(id))
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    bundle.putBoolean("success", true);
                    bundle.putSerializable("value", dataSnapshot.getValue(Customer.class));
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                })
                .addOnFailureListener(err -> {
                    bundle.putBoolean("success", false);
                    bundle.putString("error", err.getMessage());
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                });
    }

    public void getAllCustomer() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.setAction("cheetah.service.customer.getAll.result");

        Firebase.database
                .child("table_customers")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    bundle.putBoolean("success", true);
                    bundle.putSerializable("value", dataSnapshot.getValue(ArrayList.class));
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                })
                .addOnFailureListener(err -> {
                    bundle.putBoolean("success", false);
                    bundle.putString("error", err.getMessage());
                    intent.putExtras(bundle);

                    sendBroadcast(intent);
                });
    }
}
