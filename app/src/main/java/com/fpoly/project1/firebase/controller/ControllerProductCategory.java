package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.ProductCategory;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Objects;

public class ControllerProductCategory extends ControllerBase<ProductCategory> {
    public ControllerProductCategory() {
        super("table_product_categories");
    }

    @Override
    public boolean setSync(ProductCategory value, boolean update) {
        DatabaseReference tableReference = Firebase.database.child(this.table);
        DatabaseReference rowReference;

        try {
            if (!update) {
                rowReference = tableReference.push();

                value.__id = rowReference.getKey();

                Tasks.await(Firebase.database.child(this.table).child(Objects.requireNonNull(rowReference.getKey())).setValue(value));
            } else {
                rowReference = tableReference.child(value.__id);
                Tasks.await(rowReference.setValue(value));
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setAsync(ProductCategory value, boolean update, SuccessListener successListener, FailureListener failureListener) {

    }

    @Override
    public boolean removeSync(String referenceId) {
        try {
            Tasks.await(Firebase.database
                    .child(table)
                    .child(referenceId)
                    .setValue(null)
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void removeAsync(String referenceId, SuccessListener successListener, FailureListener failureListener) {

    }

    @Override
    public ProductCategory getSync(String referenceId) {
        try {
            return Tasks.await(Firebase.database
                    .child(table)
                    .child(referenceId)
                    .get()
            ).getValue(ProductCategory.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void getAsync(String referenceId, SuccessListener successListener, FailureListener failureListener) {

    }

    @Override
    public ArrayList<ProductCategory> getAllSync() {
        try {
            ArrayList<ProductCategory> list = new ArrayList<>();

            for (DataSnapshot dataSnapshot : Tasks.await(Firebase.database.child(table).get()).getChildren())
                list.add(dataSnapshot.getValue(ProductCategory.class));

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void getAllAsync(SuccessListener successListener, FailureListener failureListener) {

    }
}

