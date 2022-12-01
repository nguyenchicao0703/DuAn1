package com.fpoly.project1.firebase.controller

import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.Customer
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference

class ControllerCustomer : ControllerBase<Customer>("table_customers") {
    /**
     * Broadcast object:
     * {
     * success: boolean, <-- response status, true if success, else false
     * value: any        <-- result, if any
     * error: String     <-- error message if failed
     * }
     *
     *
     * Firebase Realtime Database document
     * Please read: https://firebase.google.com/docs/database/android/read-and-write
     */
    override fun setSync(value: Customer, update: Boolean): Boolean {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference

        return try {
            if (!update) {
                val listUsers = this.getAllSync() ?: return false
                if (listUsers.stream()
                        .anyMatch { account: Customer? -> account!!.emailAddress == value.emailAddress || account.gid == value.gid || account.fid == value.fid }
                ) return false

                rowReference = tableReference.push()

                // override ID
                value.__id = rowReference.key
                Tasks.await(
                    Firebase.database.child(table).child(value.__id!!)
                        .setValue(value)
                )
            } else {
                rowReference = tableReference.child(value.__id!!)
                Tasks.await(rowReference.setValue(value))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun addSync(value: Customer): String? {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        return try {
            val listUsers = this.getAllSync() ?: return null
            if (listUsers.stream()
                    .anyMatch { account: Customer? -> account!!.emailAddress == value.emailAddress || account.gid == value.gid || account.fid == value.fid }
            ) return null

            rowReference = tableReference.push()

            // override ID
            value.__id = rowReference.key
            Tasks.await(
                Firebase.database.child(table).child(value.__id!!)
                    .setValue(value)
            )
            value.__id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun setAsync(
        value: Customer,
        update: Boolean,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
    }

    override fun removeSync(referenceId: String?): Boolean {
        return try {
            Tasks.await(
                Firebase.database
                    .child(table)
                    .child(referenceId!!)
                    .setValue(null)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun removeAsync(
        referenceId: String?,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
    }

    override fun getSync(referenceId: String?): Customer? {
        return try {
            Tasks.await(
                Firebase.database
                    .child(table)
                    .child(referenceId!!)
                    .get()
            ).getValue(Customer::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAsync(
        referenceId: String?,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
    }

    override fun getAllSync(): ArrayList<Customer>? {
        return try {
            val list = ArrayList<Customer>()
            for (dataSnapshot in Tasks.await(Firebase.database.child(table).get()).children) {
                dataSnapshot.getValue(Customer::class.java)?.let { list.add(it) }
            }

            list
        } catch (e: Exception) {
            e.printStackTrace()

            null
        }
    }

    override fun getAllAsync(
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
    }
}