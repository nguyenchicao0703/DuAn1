package com.fpoly.project1.activity.chat

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.adapter.ChatSelectorAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerChatSession

class ChatSelector : Fragment(R.layout.chat_overview) {
    override fun onResume() {
        super.onResume()

        requireActivity().findViewById<RecyclerView>(R.id.home_chat_recyclerView).let {
            it.adapter =
                ChatSelectorAdapter(
                    requireContext(),
                    ControllerChatSession().getAllSync()!!.filter { session ->
                        session.sendingUser == SessionUser.sessionId
                    }
                )
        }
    }
}
