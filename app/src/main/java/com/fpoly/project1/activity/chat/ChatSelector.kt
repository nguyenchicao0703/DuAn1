package com.fpoly.project1.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.adapter.ChatSelectorAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerChatSession

class ChatSelector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_overview)

        findViewById<RecyclerView>(R.id.home_chat_recyclerView).let {
            it.adapter =
                ChatSelectorAdapter(
                    this,
                    ControllerChatSession().getAllSync()!!.filter { session ->
                        session.sendingUser == SessionUser.sessionId
                    }
                )
        }
    }
}
