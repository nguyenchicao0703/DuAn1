package com.fpoly.project1.activity.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.ChatMessage
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.database.DataSnapshot
import java.text.SimpleDateFormat

class ChatViewAdapter(private val context: Context, private var list: List<ChatMessage>) :
    RecyclerView.Adapter<ChatViewAdapter.ViewHolder>() {
    // variables
    private val layoutInflater = LayoutInflater.from(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chatAvatar: ImageView
        var chatMessage: TextView
        var chatDate: TextView

        init {
            chatAvatar = itemView.findViewById(R.id.item_iv_chat)
            chatMessage = itemView.findViewById(R.id.item_txt_chat_message)
            chatDate = itemView.findViewById(R.id.item_txt_chat_time)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        val message = list[position]

        // switch layout between the target user and current user
        return if (message.senderId == SessionUser.sessionId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            1 -> ViewHolder(layoutInflater.inflate(R.layout.chat_item_user_this, parent, false))
            else -> ViewHolder(layoutInflater.inflate(R.layout.chat_item_user_other, parent, false))
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get view holder for corresponding message sender
        val message = list[position]

        // chat message
        holder.chatMessage.text = message.messageContent

        // chat date
        holder.chatDate.text = SimpleDateFormat.getDateInstance().format(message.sentDate)

        // try to load the user's avatar
        Firebase.storage.child("/avatars/${message.senderId}.jpg")
            .downloadUrl
            .addOnCompleteListener { uriTask ->
                if (uriTask.isSuccessful) {
                    Glide.with(context).load(uriTask.result)
                        .into(holder.chatAvatar)
                } else {
                    ControllerCustomer().getAsync(
                        message.senderId,
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                dataSnapshot?.getValue(Customer::class.java)
                                    ?.let { user ->
                                        Glide.with(context).load(user.avatarUrl)
                                            .into(holder.chatAvatar)
                                    }
                            }
                        },
                        failureListener = null
                    )
                }
            }
    }

    fun updateList(newList: List<ChatMessage>) {
        val oldList = list
        list = newList

        // notify that new entries were added to the end of list
        notifyItemRangeInserted(oldList.size, newList.size)
    }
}
