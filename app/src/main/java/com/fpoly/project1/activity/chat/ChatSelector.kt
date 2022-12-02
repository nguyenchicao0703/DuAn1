package com.fpoly.project1.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R

class ChatSelector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_overview)
        val chatRecycler = findViewById<RecyclerView>(R.id.home_chat_recyclerView)
    }
}