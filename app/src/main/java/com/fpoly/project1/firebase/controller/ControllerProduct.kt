package com.fpoly.project1.firebase.controller

import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.Product
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference

class ControllerProduct : ControllerBase<Product>("table_products") {
    override fun setSync(value: Product, update: Boolean): Boolean {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        return try {
            if (!update) {
                rowReference = tableReference.push()
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

    override fun setAsync(
        value: Product,
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

    override fun getSync(referenceId: String?): Product? {
        return try {
            Tasks.await(
                Firebase.database
                    .child(table)
                    .child(referenceId!!)
                    .get()
            ).getValue(Product::class.java)
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

    override fun getAllSync(): ArrayList<Product>? {
        return try {
            val list = ArrayList<Product>()

            for (dataSnapshot in Tasks.await(
                Firebase.database.child(table).get()
            ).children) dataSnapshot.getValue(
                Product::class.java
            )?.let {
                list.add(
                    it
                )
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