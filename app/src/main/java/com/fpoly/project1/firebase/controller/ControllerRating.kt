package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.Rating
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.database.DatabaseReference

class ControllerRating : ControllerBase<Rating>("table_ratings") {
    override fun setSync(value: Rating, update: Boolean): Boolean {
        val tableReference = Firebase.database.child(table)
        val rowReference: DatabaseReference
        return try {
            if (!update) {
                rowReference = tableReference.push()
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
            Log.e(this.javaClass.simpleName, "Error while creating table", e)
            false
        }
    }

    override fun setAsync(
        value: Rating,
        update: Boolean,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    ) {
        return
    }

    override fun removeSync(referenceId: String?): Boolean {
        return try {
            Tasks.await(Firebase.database.child(table).child(referenceId!!).setValue(null))
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

    override fun getSync(referenceId: String?): Rating? {
        return try {
            Tasks.await(Firebase.database.child(table).child(referenceId!!).get()).getValue(
                Rating::class.java
            )
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

    override fun getAllSync(): ArrayList<Rating>? {
        return try {
            val list = ArrayList<Rating>()
            for (
            dataSnapshot in Tasks.await(
                Firebase.database.child(table).get()
            ).children
            ) dataSnapshot.getValue(
                Rating::class.java
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
        return
    }
}
