package com.fpoly.project1.firebase.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.fpoly.project1.firebase.controller.ControllerBase;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;
import com.google.firebase.database.DataSnapshot;

import java.util.Arrays;

public class ServiceCustomerHandler extends Service {
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ServiceCustomerHandler() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cheetah.service.customer.login");
        intentFilter.addAction("cheetah.service.customer.get");
        intentFilter.addAction("cheetah.service.customer.getAll");
        intentFilter.addAction("cheetah.service.customer.new");
        intentFilter.addAction("cheetah.service.customer.set");
        intentFilter.addAction("cheetah.service.customer.delete");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    // customer login
                    case "cheetah.service.customer.login": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.customer.login.result");

                        controllerCustomer.getAllCustomer(
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        Customer[] customers = dataSnapshot.getValue(Customer[].class);

                                        // null snapshot
                                        if (customers == null) {
                                            rBundle.putBoolean("success", false);
                                            rBundle.putString("error", "Snapshot is null");
                                        } else {
                                            // get email / password pair
                                            Pair<String, String> credentials = new Pair<>(
                                                    intent.getExtras().getString("email"),
                                                    intent.getExtras().getString("password", null)
                                            );

                                            // get matching account
                                            Customer[] matchingCustomer = (Customer[]) Arrays.stream(customers).filter(
                                                    c -> c.emailAddress.equals(credentials.first)
                                            ).toArray();

                                            // does not have matching account
                                            if (matchingCustomer[0] == null) {
                                                rBundle.putBoolean("success", false);
                                                rBundle.putString("error", "Account does not exist");
                                            }
                                            // account was logged in via Google
                                            else if (intent.getExtras().getString("gid") != null) {
                                                rBundle.putBoolean("success", true);
                                                rBundle.putSerializable("account", matchingCustomer[0]);
                                            }
                                            // matching password hashes
                                            else if (matchingCustomer[0].password.equals(credentials.second)) {
                                                rBundle.putBoolean("success", true);
                                                rBundle.putSerializable("account", matchingCustomer[0]);
                                            }
                                            // wrong password
                                            else {
                                                rBundle.putBoolean("success", false);
                                                rBundle.putString("error", "Incorrect password");
                                            }
                                        }

                                        rIntent.putExtras(rBundle);
                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerBase.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                });
                        break;
                    }

                    // get single customer
                    case "cheetah.service.customer.get": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.customer.get.result");

                        controllerCustomer.getCustomer(intent.getExtras().getInt("value"),
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        rBundle.putBoolean("success", true);
                                        rBundle.putSerializable("value", dataSnapshot.getValue(Customer.class));
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerBase.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                });
                        break;
                    }

                    // get all customer entries
                    case "cheetah.service.customer.getAll": {
                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.customer.getAll.result");

                        controllerCustomer.getAllCustomer(
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        rBundle.putBoolean("success", true);
                                        rBundle.putSerializable("value", dataSnapshot.getValue(Customer[].class));
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerBase.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                }
                        );
                        break;
                    }

                    // insert new customer entry
                    case "cheetah.service.customer.new": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.customer.new.result");

                        controllerCustomer.newCustomer((Customer) intent.getExtras().getSerializable("value"),
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run() {
                                        rBundle.putBoolean("success", true);
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerBase.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                });
                        break;
                    }

                    // override old customer entry
                    case "cheetah.service.customer.set": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.customer.set.result");

                        controllerCustomer.setCustomer((Customer) intent.getExtras().getSerializable("value"), true,
                                new ControllerCustomer.SuccessListener() {
                                    @Override
                                    public void run() {
                                        rBundle.putBoolean("success", true);
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerCustomer.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                });
                        break;
                    }

                    // delete old customer entry
                    case "cheetah.service.customer.delete": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.customer.delete.result");

                        controllerCustomer.deleteCustomer(intent.getExtras().getInt("value"),
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run() {
                                        rBundle.putBoolean("success", true);
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerBase.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                });
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
}
