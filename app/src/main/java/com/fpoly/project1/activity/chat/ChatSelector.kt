package com.fpoly.project1.activity.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fpoly.project1.R
import androidx.recyclerview.widget.RecyclerView

class ChatSelector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_chat)
        val chatRecycler = findViewById<RecyclerView>(R.id.home_chat_recyclerView)
    }
}