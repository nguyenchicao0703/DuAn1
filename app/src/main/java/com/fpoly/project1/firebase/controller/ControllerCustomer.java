package com.fpoly.project1.firebase.controller;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.model.Customer;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Objects;

public class ControllerCustomer extends ControllerBase<Customer> {
    public ControllerCustomer() {
        super("table_customers");
    }

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

    @Override
    public boolean setSync(Customer value, boolean update) {
        DatabaseReference tableReference = Firebase.database.child(this.table);
        DatabaseReference rowReference;

        try {
            if (!update) {
                if (this.getAllSync().stream().anyMatch(account ->
                        account.emailAddress.equals(value.emailAddress) ||
                                account.gid.equals(value.gid) ||
                                account.fid.equals(value.fid))
                ) throw new Exception("Email đã tồn tại");

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

    public String addSync(Customer value) {
        DatabaseReference tableReference = Firebase.database.child(this.table);
        DatabaseReference rowReference;

        try {
            if (this.getAllSync().stream().anyMatch(account ->
                    account.emailAddress.equals(value.emailAddress) ||
                            account.gid.equals(value.gid) ||
                            account.fid.equals(value.fid))
            ) throw new Exception("Email đã tồn tại");

            rowReference = tableReference.push();

            // override ID
            value.__id = rowReference.getKey();

            Tasks.await(Firebase.database.child(this.table).child(Objects.requireNonNull(rowReference.getKey())).setValue(value));

            return value.__id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setAsync(Customer value, boolean update, SuccessListener successListener, FailureListener failureListener) {

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
    public Customer getSync(String referenceId) {
        try {
            return Tasks.await(Firebase.database
                    .child(table)
                    .child(referenceId)
                    .get()
            ).getValue(Customer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void getAsync(String referenceId, SuccessListener successListener, FailureListener failureListener) {

    }

    @Override
    public ArrayList<Customer> getAllSync() {
        try {
            ArrayList<Customer> list = new ArrayList<>();

            for (DataSnapshot dataSnapshot : Tasks.await(Firebase.database.child(table).get()).getChildren()) {
                list.add(dataSnapshot.getValue(Customer.class));
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
