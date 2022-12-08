package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.Product
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.database.DatabaseReference

class ControllerProduct : ControllerBase<Product>("table_products") {
    override fun setSync(value: Product, update: Boolean): Boolean {
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
        value: Product,
        update: Boolean,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        if (update) {
            rowReference = tableReference.push()
            value.id = rowReference.key

            rowReference.setValue(value).addOnCompleteListener {
                if (it.isSuccessful)
                    successListener?.run(it.result)
                else
                    failureListener?.run(it.exception)
            }
        } else {
            tableReference.child(value.id!!).setValue(value)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        successListener?.run(it.result)
                    else
                        failureListener?.run(it.exception)
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

    override fun getSync(referenceId: String?): Product? {
        return try {
            Tasks.await(
                Firebase.database
                    .child(table)
                    .child(referenceId!!)
                    .get()
            ).getValue(Product::class.java)
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while getting $referenceId", e)

            null
        }
    }

    override fun getAllSync(): ArrayList<Product>? {
        return try {
            val list = ArrayList<Product>()

            for (
            dataSnapshot in Tasks.await(
                Firebase.database.child(table).get()
            ).children
            ) dataSnapshot.getValue(
                Product::class.java
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
