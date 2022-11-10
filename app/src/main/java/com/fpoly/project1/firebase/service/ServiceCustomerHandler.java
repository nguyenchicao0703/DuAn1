package com.fpoly.project1.firebase.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.fpoly.project1.firebase.controller.ControllerBase;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.model.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        intentFilter.addAction(ServiceType.CUSTOMER_LOGIN);
        intentFilter.addAction(ServiceType.CUSTOMER_GET);
        intentFilter.addAction(ServiceType.CUSTOMER_GETALL);
        intentFilter.addAction(ServiceType.CUSTOMER_NEW);
        intentFilter.addAction(ServiceType.CUSTOMER_SET);
        intentFilter.addAction(ServiceType.CUSTOMER_DELETE);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    // customer login
                    case ServiceType.CUSTOMER_LOGIN: {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction(ServiceType.CUSTOMER_LOGIN_RESULT);

                        controllerCustomer.getAllCustomer(
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        Customer[] customers = dataSnapshot.getValue(Customer[].class);

                                        // null snapshot
                                        if (customers == null) {
                                            rBundle.putBoolean("success", false);
                                            rBundle.putString("error", "Snapshot is null");
                                            rIntent.putExtras(rBundle);

                                            sendBroadcast(rIntent);
                                        } else {
                                            // we will log user in via firebase since it include both
                                            // password-based auth and google auth,
                                            // here we're just creating a customer object linked to
                                            // the firebase account

                                            // get current firebase account, at this point it is non-null
                                            FirebaseUser googleAccount = FirebaseAuth.getInstance().getCurrentUser();
                                            assert googleAccount != null;

                                            // get matching account
                                            Customer[] matchingCustomer = (Customer[]) Arrays.stream(customers).filter(
                                                    c -> c.emailAddress.equals(googleAccount.getEmail())
                                            ).toArray();

                                            if (matchingCustomer[0] == null) {
                                                Customer customerAccount =
                                                        new Customer(
                                                                null,
                                                                googleAccount.getUid(),
                                                                googleAccount.getPhotoUrl().toString(),
                                                                googleAccount.getDisplayName(),
                                                                null,
                                                                googleAccount.getEmail(),
                                                                null
                                                        );

                                                controllerCustomer.newCustomer(customerAccount,
                                                        new ControllerBase.SuccessListener() {
                                                            @Override
                                                            public void run() {
                                                                Log.i(ServiceType.CUSTOMER_LOGIN, "Added account to Firebase");

                                                                rBundle.putBoolean("success", true);
                                                                //rBundle.putSerializable("account", customerAccount);
                                                                rIntent.putExtras(rBundle);

                                                                sendBroadcast(rIntent);
                                                            }
                                                        },
                                                        new ControllerBase.FailureListener() {
                                                            @Override
                                                            public void run(Exception error) {
                                                                Log.i(ServiceType.CUSTOMER_LOGIN, "Failed to add account to Firebase");
                                                                error.printStackTrace();

                                                                rBundle.putBoolean("success", false);
                                                                rBundle.putString("error", error.getMessage());
                                                                rIntent.putExtras(rBundle);

                                                                sendBroadcast(rIntent);
                                                            }
                                                        });
                                            } else {
                                                Log.i(ServiceType.CUSTOMER_LOGIN, "Fetched account from Firebase");

                                                rBundle.putBoolean("success", true);
                                                //rBundle.putSerializable("account", matchingCustomer[0]);
                                                rIntent.putExtras(rBundle);

                                                sendBroadcast(rIntent);
                                            }
                                        }
                                    }
                                },
                                new ControllerBase.FailureListener() {
                                    @Override
                                    public void run(Exception error) {
                                        Log.i(ServiceType.CUSTOMER_LOGIN, "Failed to get account from Firebase");
                                        error.printStackTrace();

                                        rBundle.putBoolean("success", false);
                                        rBundle.putString("error", error.getMessage());
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                });
                        break;
                    }

                    // get single customer
                    case ServiceType.CUSTOMER_GET: {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction(ServiceType.CUSTOMER_GET_RESULT);

                        controllerCustomer.getCustomer(intent.getExtras().getInt("value"),
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        rBundle.putBoolean("success", true);
                                        //rBundle.putSerializable("value", dataSnapshot.getValue(Customer.class));
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
                    case ServiceType.CUSTOMER_GETALL: {
                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction(ServiceType.CUSTOMER_GETALL_RESULT);

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

                    // OBSOLETE
                    // insert new customer entry
                    case ServiceType.CUSTOMER_NEW: {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction(ServiceType.CUSTOMER_NEW_RESULT);

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
                    case ServiceType.CUSTOMER_SET: {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction(ServiceType.CUSTOMER_SET_RESULT);

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
                    case ServiceType.CUSTOMER_DELETE: {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction(ServiceType.CUSTOMER_DELETE_RESULT);

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
