package com.fpoly.project1.firebase.controller

import android.util.Log
import com.fpoly.project1.firebase.Firebase
import com.google.firebase.database.DataSnapshot

abstract class ControllerBase<T>(val table: String) {
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
                            if (task2.isSuccessful)
                                Log.i("${this.javaClass.simpleName} - $table", "Created table")
                            else
                                Log.e(
                                    "${this.javaClass.simpleName} - $table",
                                    "Error while creating table",
                                    task2.exception
                                )
                        }
                } else if (!task.isSuccessful) {
                    Log.e(
                        "${this.javaClass.simpleName} - $table",
                        "Error while creating table",
                        task.exception
                    )
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
    open fun removeAsync(
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

    // Get value at index
    abstract fun getSync(referenceId: String?): T?
    open fun getAsync(
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

    // Get all values from table
    abstract fun getAllSync(): ArrayList<T>?
    open fun getAllAsync(successListener: SuccessListener?, failureListener: FailureListener?) {
        Firebase.database.child(table).get()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    successListener?.run(it.result)
                else
                    failureListener?.run(it.exception)
            }
    }

    // Success listener
    abstract class SuccessListener {
        open fun run() {
            return
        }

        open fun run(unused: Any?) {
            return
        }

        open fun run(dataSnapshot: DataSnapshot?) {
            return
        }
    }

    // Failure listener
    abstract class FailureListener {
        open fun run(error: Exception?) {
            return
        }
    }
}
