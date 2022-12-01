package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.google.firebase.database.DataSnapshot

abstract class ControllerBase<T>(protected val table: String) {
    init {
        Firebase.database
            .child(table)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.value == null) {
                    Firebase.database
                        .child(table)
                        .setValue(0)
                        .addOnCompleteListener { task2 ->
                            Log.i(
                                "Controller - $table",
                                (if (task2.isSuccessful) "Created table" else task2.exception!!.message)!!
                            )
                        }
                } else {
                    if (task.exception != null) task.exception!!.printStackTrace()
                }
            }
    }

    // Set new value or update existing one
    abstract fun setSync(value: T, update: Boolean): Boolean
    abstract fun setAsync(
        value: T,
        update: Boolean,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    )

    // Remove value at index
    abstract fun removeSync(referenceId: String?): Boolean
    abstract fun removeAsync(
        referenceId: String?,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    )

    // Get value at index
    abstract fun getSync(referenceId: String?): T?
    abstract fun getAsync(
        referenceId: String?,
        successListener: SuccessListener?,
        failureListener: FailureListener?
    )

    // Get all values from table
    abstract fun getAllSync(): ArrayList<T>?
    abstract fun getAllAsync(successListener: SuccessListener?, failureListener: FailureListener?)

    // Success listener
    abstract class SuccessListener {
        fun run() {}
        fun run(unused: Any?) {}
        fun run(dataSnapshot: DataSnapshot?) {}
    }

    // Failure listener
    abstract class FailureListener {
        abstract fun run(error: Exception?)
    }
}