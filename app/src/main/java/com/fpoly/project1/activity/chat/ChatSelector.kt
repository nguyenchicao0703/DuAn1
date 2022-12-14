package com.fpoly.project1.activity.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var emptyView: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_overview, container, false)
        view.alpha = 0f

        chatRecyclerView = view.findViewById(R.id.home_chat_recyclerView)
        emptyView = view.findViewById(R.id.home_chat_empty_view)

        return view
    }

    override fun onStart() {
        super.onStart()

        chatRecyclerView.let {
            ControllerChatSession().getAllAsync(
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        val sessions = ArrayList<ChatSession>()
                        dataSnapshot?.children?.forEach { entry ->
                            sessions.add(entry.getValue(ChatSession::class.java)!!)
                        }

                        val matchingSessions = sessions.filter { session ->
                            session.id!!.contains(SessionUser.sessionId!!)
                        }

                        if (context != null && matchingSessions.isNotEmpty()) {
                            chatRecyclerView.visibility = View.VISIBLE
                            emptyView.visibility = View.GONE

                            it.adapter = ChatSelectorAdapter(requireContext(), matchingSessions)
                        }

                        view?.animate()?.alpha(1f)
                    }
                },
                failureListener = object : ControllerBase.FailureListener() {
                    override fun run(error: Exception?) {
                        Toast.makeText(
                            requireContext(), "Failed to retrieve data from server",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.e(this@ChatSelector::class.simpleName, "Error", error)
                    }
                }
            )
        }
    }
}
