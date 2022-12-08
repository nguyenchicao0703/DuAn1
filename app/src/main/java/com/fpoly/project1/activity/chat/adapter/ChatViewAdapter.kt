package com.fpoly.project1.activity.chat.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.ChatMessage
import com.fpoly.project1.firebase.model.Customer
import java.text.SimpleDateFormat

class ChatViewAdapter(private val context: Context, private var list: List<ChatMessage>) :
    RecyclerView.Adapter<ChatViewAdapter.ViewHolder>() {
    // variables
    private var targetUser: Customer? = null
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
        return position % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            1 -> ViewHolder(layoutInflater.inflate(R.layout.chat_item_user_this, parent))
            else -> ViewHolder(layoutInflater.inflate(R.layout.chat_item_user_other, parent))
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get view holder for corresponding message sender
        val message = list[position]

        // bind target user
        if (targetUser == null)
            targetUser = ControllerCustomer().getSync(message.senderId)

        // check for view type and load corresponding avatar
        when (holder.itemViewType) {
            1 -> {
                // bind data
                SessionUser.avatar.addOnCompleteListener { uri ->
                    Glide.with(context).load(uri.result)
                        .into((context as Activity).findViewById(R.id.profile_iv_avt))
                }
            }
            else -> {
                // bind data
                Glide.with(context)
                    .load(targetUser!!.avatarUrl)
                    .into(holder.chatAvatar)
            }
        }
        holder.chatMessage.text = message.messageContent
        holder.chatDate.text = SimpleDateFormat.getDateInstance().format(message.sentDate)
    }

    fun updateList(newList: List<ChatMessage>) {
        notifyItemRangeRemoved(0, list.size)
        list = newList
        notifyItemRangeInserted(0, newList.size)
    }
}
