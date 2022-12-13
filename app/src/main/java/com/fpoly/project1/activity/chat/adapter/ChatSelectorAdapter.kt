package com.fpoly.project1.activity.chat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.ChatSession
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.database.DataSnapshot
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class ChatSelectorAdapter(
    private val context: Context,
    private var sessions: List<ChatSession>,
) : RecyclerView.Adapter<ChatSelectorAdapter.ViewHolder>() {

    private val controllerCustomer = ControllerCustomer()
    private val layoutInflater = LayoutInflater.from(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chatAvatar: CircleImageView = itemView.findViewById(R.id.item_chat_iv_avatar)
        var chatName: TextView = itemView.findViewById(R.id.item_chat_txt_name)
        var chatMessage: TextView = itemView.findViewById(R.id.item_chat_txt_content)
        var chatDate: TextView = itemView.findViewById(R.id.item_chat_txt_time)

        init {
            itemView.alpha = 0f
            itemView.translationY = 50f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.chat_item_selector, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get session at position
        val session = sessions[position]

        // get last message, if any
        val lastMessage = session.messages?.last()

        // get the target user ID of this session
        val userId =
            if (SessionUser.sessionId == session.sendingUser)
                session.targetUser
            else
                session.sendingUser

        // get the target user object then process
        controllerCustomer.getAsync(
            userId,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    // user object
                    val user = dataSnapshot?.getValue(Customer::class.java)!!

                    // try to load the user avatar from storage, else fallback to avatarUrl
                    Firebase.storage.child("/avatars/${user.id}.jpg").downloadUrl
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                Glide.with(context)
                                    .load(task.result)
                                    .into(holder.chatAvatar)
                            else
                                Glide.with(context)
                                    .load(user.avatarUrl)
                                    .into(holder.chatAvatar)
                        }

                    // user full name
                    holder.chatName.text = user.fullName

                    // last chat message, if any
                    holder.chatMessage.text =
                        if (lastMessage?.senderId == SessionUser.sessionId)
                            "Me: ${lastMessage?.messageContent}"
                        else
                            lastMessage?.messageContent ?: "No recent messages"

                    // sent date
                    if (lastMessage?.sentDate != null) {
                        holder.chatDate.text =
                            SimpleDateFormat.getDateInstance()
                                .format(lastMessage.sentDate!!)
                    }

                    // click to start chat session activity
                    holder.itemView.setOnClickListener {
                        val intentData = Intent(context, ChatView::class.java)
                        intentData.putExtras(bundleOf(Pair("id", userId)))

                        context.startActivity(intentData)
                    }

                    // show view
                    holder.itemView.animate()
                        .alpha(1f)
                        .translationY(0f)
                }
            },
            failureListener = object : ControllerBase.FailureListener() {
                override fun run(error: Exception?) {
                    holder.chatName.text = "Unable to retrieve data"
                    holder.chatMessage.text = error?.localizedMessage ?: "Unknown error"
                }
            }
        )
    }

    override fun getItemCount(): Int = sessions.size
}
