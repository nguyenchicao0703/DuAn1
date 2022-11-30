package com.fpoly.project1.activity.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fpoly.project1.R;

public class ChatSelector extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chat);

        RecyclerView chatRecycler = findViewById(R.id.home_chat_recyclerView);
    }
}
