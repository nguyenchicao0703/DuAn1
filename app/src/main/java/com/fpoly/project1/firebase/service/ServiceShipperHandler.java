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
import com.fpoly.project1.firebase.controller.ControllerShipper;
import com.fpoly.project1.firebase.model.Shipper;
import com.google.firebase.database.DataSnapshot;

import java.util.Arrays;

public class ServiceShipperHandler extends Service {
    private final ControllerShipper controllerShipper = new ControllerShipper();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ServiceShipperHandler() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cheetah.service.shipper.login");
        intentFilter.addAction("cheetah.service.shipper.get");
        intentFilter.addAction("cheetah.service.shipper.getAll");
        intentFilter.addAction("cheetah.service.shipper.new");
        intentFilter.addAction("cheetah.service.shipper.set");
        intentFilter.addAction("cheetah.service.shipper.delete");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    // shipper login
                    case "cheetah.service.shipper.login": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.shipper.login.result");

                        controllerShipper.getAllShipper(
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        Shipper[] shippers = dataSnapshot.getValue(Shipper[].class);

                                        // null snapshot
                                        if (shippers == null) {
                                            rBundle.putBoolean("success", false);
                                            rBundle.putString("error", "Snapshot is null");
                                        } else {
                                            // get username / password pair
                                            Pair<String, String> credentials = new Pair<>(
                                                    intent.getExtras().getString("username"),
                                                    intent.getExtras().getString("password", null)
                                            );

                                            // get matching account
                                            Shipper[] matchingShipper = (Shipper[]) Arrays.stream(shippers).filter(
                                                    c -> c.cre_username.equals(credentials.first)
                                            ).toArray();

                                            // does not have matching account
                                            if (matchingShipper[0] == null) {
                                                rBundle.putBoolean("success", false);
                                                rBundle.putString("error", "Account does not exist");
                                            }
                                            // matching password hashes
                                            else if (matchingShipper[0].cre_password.equals(credentials.second)) {
                                                rBundle.putBoolean("success", true);
                                                rBundle.putSerializable("account", matchingShipper[0]);
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

                    // get single shipper
                    case "cheetah.service.shipper.get": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.shipper.get.result");

                        controllerShipper.getShipper(intent.getExtras().getInt("value"),
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        rBundle.putBoolean("success", true);
                                        rBundle.putSerializable("value", dataSnapshot.getValue(Shipper.class));
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

                    // get all shipper entries
                    case "cheetah.service.shipper.getAll": {
                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.shipper.getAll.result");

                        controllerShipper.getAllShipper(
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        rBundle.putBoolean("success", true);
                                        rBundle.putSerializable("value", dataSnapshot.getValue(Shipper[].class));
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

                    // insert new shipper entry
                    case "cheetah.service.shipper.new": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.shipper.new.result");

                        controllerShipper.newShipper((Shipper) intent.getExtras().getSerializable("value"),
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

                    // override old shipper entry
                    case "cheetah.service.shipper.set": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.shipper.set.result");

                        controllerShipper.setShipper((Shipper) intent.getExtras().getSerializable("value"), true,
                                new ControllerShipper.SuccessListener() {
                                    @Override
                                    public void run() {
                                        rBundle.putBoolean("success", true);
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerShipper.FailureListener() {
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

                    // delete old shipper entry
                    case "cheetah.service.shipper.delete": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.shipper.delete.result");

                        controllerShipper.deleteShipper(intent.getExtras().getInt("value"),
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
