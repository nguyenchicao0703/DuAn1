package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.database.DatabaseReference

class ControllerProductCategory : ControllerBase<ProductCategory>("table_product_categories") {
    override fun setSync(value: ProductCategory, update: Boolean): Boolean {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        return try {
            if (!update) {
                rowReference = tableReference.push()
                value.id = rowReference.key
                Tasks.await(
                    Firebase.database.child(table).child(value.id!!).setValue(value)
                )
            } else {
                rowReference = tableReference.child(value.id!!)
                Tasks.await(rowReference.setValue(value))
            }
            true
        } catch (e: FirebaseException) {
            Log.e(this.javaClass.simpleName, "Error while setting value", e)
            false
        }
    }

    override fun setAsync(
        value: ProductCategory,
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
                    successListener?.run()
                else
                    failureListener?.run(it.exception)
            }
        } else {
            tableReference.child(value.id!!).setValue(value)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        successListener?.run()
                    else
                        failureListener?.run(it.exception)
                }
        }
    }

    override fun removeSync(referenceId: String?): Boolean {
        return try {
            Tasks.await(
                Firebase.database.child(table).child(referenceId!!).setValue(null)
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
        Firebase.database.child(table).child(referenceId!!).setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    successListener?.run()
                else
                    failureListener?.run(it.exception)
            }
    }

    override fun getSync(referenceId: String?): ProductCategory? {
        return try {
            Tasks.await(
                Firebase.database.child(table).child(referenceId!!).get()
            ).getValue(ProductCategory::class.java)
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
        Firebase.database.child(table).child(referenceId!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    successListener?.run(it.result)
                else
                    failureListener?.run(it.exception)
            }
    }

    override fun getAllSync(): ArrayList<ProductCategory>? {
        return try {
            val list = ArrayList<ProductCategory>()

            for (
            dataSnapshot in Tasks.await(
                Firebase.database.child(table).get()
            ).children
            ) dataSnapshot.getValue(
                ProductCategory::class.java
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

    override fun getAllAsync(
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        Firebase.database.child(table).get()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    successListener?.run(it.result)
                else
                    failureListener?.run(it.exception)
            }
    }
}
