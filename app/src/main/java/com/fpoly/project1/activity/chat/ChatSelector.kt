package com.fpoly.project1.activity.chat

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.adapter.ChatSelectorAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerChatSession
import com.fpoly.project1.firebase.model.ChatSession
import com.google.firebase.database.DataSnapshot

class ChatSelector : Fragment(R.layout.chat_overview) {
    override fun onResume() {
        super.onResume()

        requireActivity().findViewById<RecyclerView>(R.id.home_chat_recyclerView).let {
            ControllerChatSession().getAllAsync(
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        val sessions = ArrayList<ChatSession>()
                        dataSnapshot?.children?.forEach { entry ->
                            sessions.add(entry.getValue(ChatSession::class.java)!!)
                        }

                        it.adapter =
                            ChatSelectorAdapter(
                                requireContext(),
                                sessions.filter { session ->
                                    session.id!!.contains(SessionUser.sessionId!!)
                                }
                            )
                    }
                },
                failureListener = object : ControllerBase.FailureListener() {
                    override fun run(error: Exception?) {
                        Toast.makeText(
                            requireContext(), "Failed to retrieve data from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}
