package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.Customer
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.newFixedThreadPoolContext

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
                value.id = rowReference.key
                Tasks.await(
                    Firebase.database.child(table).child(value.id!!)
                        .setValue(value)
                )
            } else {
                rowReference = tableReference.child(value.id!!)
                Tasks.await(rowReference.setValue(value))
            }
            true
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while setting ${value.id}", e)

            false
        }
    }

    fun addSync(value: Customer): String? {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        return try {
            val listUsers = this.getAllSync() ?: return null
            if (listUsers.stream()
                    .anyMatch { account: Customer? ->
                        account!!.emailAddress == value.emailAddress ||
                                account.gid == value.gid ||
                                account.fid == value.fid
                    }
            ) {
                null
            } else {
                rowReference = tableReference.push()

                // override ID
                value.id = rowReference.key
                Tasks.await(
                    Firebase.database.child(table).child(value.id!!)
                        .setValue(value)
                )
                value.id
            }
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while adding ${value.id}", e)

            null
        }
    }

    fun addAsync(value: Customer, successListener: SuccessListener?, failureListener: FailureListener?) {
        getAllAsync(
            successListener = object: SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val customers = ArrayList<Customer>()
                    if (dataSnapshot != null)
                        for (entry in dataSnapshot.children)
                            customers.add(entry.getValue(Customer::class.java)!!)

                    if (customers.stream().anyMatch {
                            it!!.emailAddress == value.emailAddress ||
                                    it.gid == value.gid ||
                                    it.fid == value.fid
                        }) {
                        failureListener?.run(Exception("Entry already exists"))
                    } else {
                        val rowReference = Firebase.database.child(table).push()

                        // override ID
                        value.id = rowReference.key
                        Firebase.database.child(table).child(value.id!!)
                            .setValue(value)
                            .addOnCompleteListener {
                                if (it.isSuccessful)
                                    successListener?.run(value.id)
                                else
                                    failureListener?.run(it.exception)
                            }
                    }
                }
            },
            failureListener
        )
    }

    override fun setAsync(
        value: Customer,
        update: Boolean,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        return
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
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while removing $referenceId", e)

            false
        }
    }

    override fun removeAsync(
        referenceId: String?,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        return
    }

    override fun getSync(referenceId: String?): Customer? {
        return try {
            Tasks.await(
                Firebase.database
                    .child(table)
                    .child(referenceId!!)
                    .get()
            ).getValue(Customer::class.java)
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while getting $referenceId", e)

            null
        }
    }

    override fun getAsync(
        referenceId: String?,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        return
    }

    override fun getAllSync(): ArrayList<Customer>? {
        return try {
            val list = ArrayList<Customer>()
            for (dataSnapshot in Tasks.await(Firebase.database.child(table).get()).children) {
                dataSnapshot.getValue(Customer::class.java)?.let { list.add(it) }
            }

            list
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while getting all entries", e)

            null
        }
    }

    override fun getAllAsync(
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        Firebase.database.child(table).get().addOnCompleteListener {
            if (it.isSuccessful)
                successListener?.run(it.result)
            else
                failureListener?.run(it.exception)
        }
    }
}
