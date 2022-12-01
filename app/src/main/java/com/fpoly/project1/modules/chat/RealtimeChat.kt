package com.fpoly.project1.modules.chat

import com.fpoly.project1.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.fpoly.project1.firebase.controller.ControllerBase.SuccessListener
import com.fpoly.project1.firebase.controller.ControllerBase.FailureListener
import android.util.Log
import com.google.android.gms.tasks.Task
import java.util.HashMap

class RealtimeChat {
    private val databaseReference = Firebase.database.child("chat_sessions")

    init {
        databaseReference.get()
            .addOnCompleteListener { task: Task<DataSnapshot?> ->
                if (!task.isSuccessful) {
                    databaseReference.setValue(0)
                        .addOnCompleteListener { task2: Task<Void?> ->
                            Log.i(
                                "RealtimeChat",
                                (if (task2.isSuccessful) "Created table" else task2.exception!!.message)!!
                            )
                        }
                }
            }
    }

    fun addChatSession(
        uid1: String?,
        uid2: String?,
        sListener: SuccessListener,
        fListener: FailureListener
    ) {
        val newReference = databaseReference.push()
        databaseReference.setValue(ChatSession(newReference.key, uid1, uid2, HashMap()))
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    sListener.run()
                } else {
                    fListener.run(task.exception)
                }
            }
    }
}