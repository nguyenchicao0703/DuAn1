package com.fpoly.project1.activity.chat

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.project1.R

class ChatView : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
		setContentView(R.layout.chat_session)
	}
}