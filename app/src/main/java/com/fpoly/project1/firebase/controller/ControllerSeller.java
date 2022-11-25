package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Seller;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Objects;

public class ControllerSeller extends ControllerBase<Seller> {
    public ControllerSeller() {
        super("table_sellers");
    }

    @Override
    public boolean setSync(Seller value, boolean update) {
        DatabaseReference tableReference = Firebase.database.child(this.table);
        DatabaseReference rowReference;

        try {
            if (!update) {
                if (this.getAllSync().stream().anyMatch(account ->
                        account.emailAddress.equals(value.emailAddress)
                )) throw new Exception("Email đã tồn tại");

                rowReference = tableReference.push();

                // override ID
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
    public void setAsync(Seller value, boolean update, SuccessListener successListener, FailureListener failureListener) {

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
    public Seller getSync(String referenceId) {
        try {
            return Tasks.await(Firebase.database
                    .child(table)
                    .child(referenceId)
                    .get()
            ).getValue(Seller.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void getAsync(String referenceId, SuccessListener successListener, FailureListener failureListener) {

    }

    @Override
    public ArrayList<Seller> getAllSync() {
        try {
            ArrayList<Seller> list = new ArrayList<>();

            for (DataSnapshot dataSnapshot : Tasks.await(Firebase.database.child(table).get()).getChildren()) {
                list.add(dataSnapshot.getValue(Seller.class));
            }

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
