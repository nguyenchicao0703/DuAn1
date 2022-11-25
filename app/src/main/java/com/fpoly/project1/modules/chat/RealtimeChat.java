package com.fpoly.project1.modules.chat;

import android.util.Log;

import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.controller.ControllerBase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class RealtimeChat {
    private final DatabaseReference databaseReference = Firebase.database.child("chat_sessions");

    public RealtimeChat() {
        databaseReference.get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        databaseReference.setValue(0)
                                .addOnCompleteListener(task2 ->
                                    Log.i("RealtimeChat", task2.isSuccessful() ? "Created table" : task2.getException().getMessage())
                                );
                    }
                });
    }

    public void addChatSession(String uid1, String uid2, ControllerBase.SuccessListener sListener, ControllerBase.FailureListener fListener) {
        DatabaseReference newReference = databaseReference.push();
        databaseReference.setValue(new ChatSession(newReference.getKey(), uid1, uid2, new HashMap<>()))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sListener.run();
                    } else {
                        fListener.run(task.getException());
                    }
                });
    }
}
