package com.fpoly.project1.firebase.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.fpoly.project1.firebase.controller.ControllerBase;
import com.fpoly.project1.firebase.controller.ControllerProduct;
import com.fpoly.project1.firebase.model.Product;
import com.google.firebase.database.DataSnapshot;

import java.util.Arrays;

public class ServiceProductHandler extends Service {
    private final ControllerProduct controllerProduct = new ControllerProduct();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ServiceProductHandler() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cheetah.service.product.get");
        intentFilter.addAction("cheetah.service.product.getAll");
        intentFilter.addAction("cheetah.service.product.new");
        intentFilter.addAction("cheetah.service.product.set");
        intentFilter.addAction("cheetah.service.product.delete");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    // get single product
                    case "cheetah.service.product.get": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.product.get.result");

                        controllerProduct.getProduct(intent.getExtras().getInt("value"),
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        rBundle.putBoolean("success", true);
                                        rBundle.putSerializable("value", dataSnapshot.getValue(Product.class));
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

                    // get all product entries
                    case "cheetah.service.product.getAll": {
                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.product.getAll.result");

                        controllerProduct.getAllProduct(
                                new ControllerBase.SuccessListener() {
                                    @Override
                                    public void run(DataSnapshot dataSnapshot) {
                                        Product[] products = dataSnapshot.getValue(Product[].class);

                                        if (products == null) {
                                            rBundle.putBoolean("success", false);
                                            rBundle.putString("error", "Snapshot is null");
                                        } else {
                                            rBundle.putBoolean("success", true);
                                            rBundle.putSerializable("value", products);
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
                                }
                        );
                        break;
                    }

                    // insert new product entry
                    case "cheetah.service.product.new": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.product.new.result");

                        controllerProduct.newProduct((Product) intent.getExtras().getSerializable("value"),
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

                    // override old product entry
                    case "cheetah.service.product.set": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.product.set.result");

                        controllerProduct.setProduct((Product) intent.getExtras().getSerializable("value"), true,
                                new ControllerProduct.SuccessListener() {
                                    @Override
                                    public void run() {
                                        rBundle.putBoolean("success", true);
                                        rIntent.putExtras(rBundle);

                                        sendBroadcast(rIntent);
                                    }
                                },
                                new ControllerProduct.FailureListener() {
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

                    // delete old product entry
                    case "cheetah.service.product.delete": {
                        if (intent.getExtras() == null)
                            return;

                        Intent rIntent = new Intent();
                        Bundle rBundle = new Bundle();
                        rIntent.setAction("cheetah.service.product.delete.result");

                        controllerProduct.deleteProduct(intent.getExtras().getInt("value"),
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
