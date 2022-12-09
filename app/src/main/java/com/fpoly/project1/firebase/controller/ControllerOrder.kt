package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.Order
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.database.DatabaseReference

class ControllerOrder : ControllerBase<Order>("table_orders") {
    override fun setSync(value: Order, update: Boolean): Boolean {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        return try {
            if (!update) {
                rowReference = tableReference.push()
                value.id = rowReference.key!!
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

    override fun setAsync(
        value: Order,
        update: Boolean,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference

        if (!update) {
            rowReference = tableReference.push()
            value.id = rowReference.key!!
            Firebase.database.child(table).child(value.id!!).setValue(value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        successListener?.run()
                    else
                        failureListener?.run(task.exception)
                }
        } else {
            rowReference = tableReference.child(value.id!!)
            rowReference.setValue(value).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    successListener?.run()
                else
                    failureListener?.run(task.exception)
            }
        }
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

    override fun getSync(referenceId: String?): Order? {
        return try {
            Tasks.await(
                Firebase.database
                    .child(table)
                    .child(referenceId!!)
                    .get()
            ).getValue(Order::class.java)
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while getting $referenceId", e)

            null
        }
    }

    override fun getAllSync(): ArrayList<Order>? {
        return try {
            val list = ArrayList<Order>()

            for (
            dataSnapshot in Tasks.await(
                Firebase.database.child(table).get()
            ).children
            ) dataSnapshot.getValue(
                Order::class.java
            )?.let {
                list.add(
                    it
                )
            }

            list
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while getting all entries", e)

            null
        }
    }
}
