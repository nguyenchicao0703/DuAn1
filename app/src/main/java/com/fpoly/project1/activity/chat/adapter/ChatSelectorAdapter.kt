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
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.ChatSession
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class ChatSelectorAdapter(
	private val context: Context,
	private var list: List<ChatSession>
) : RecyclerView.Adapter<ChatSelectorAdapter.ViewHolder>() {
	private val controllerCustomer = ControllerCustomer()
	private val layoutInflater = LayoutInflater.from(context)

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var chatAvatar: CircleImageView = itemView.findViewById(R.id.item_chat_iv_avatar)
		var chatName: TextView = itemView.findViewById(R.id.item_chat_txt_name)
		var chatMessage: TextView = itemView.findViewById(R.id.item_chat_txt_content)
		var chatDate: TextView = itemView.findViewById(R.id.item_chat_txt_time)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
		ViewHolder(layoutInflater.inflate(R.layout.chat_item_selector, parent))

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val session = list[position]
		val lastMessage = session.messages?.last()
		val lastSender = controllerCustomer.getSync(lastMessage?.senderId)

		if (lastSender != null) {
			Glide.with(context)
				.load(controllerCustomer.getSync(lastSender.avatarUrl))
				.into(holder.chatAvatar)
			holder.chatName.text = lastSender.fullName
			holder.chatMessage.text = lastMessage?.messageContent
			holder.chatDate.text = SimpleDateFormat.getDateInstance().format(lastMessage?.sentDate)
			holder.itemView.setOnClickListener {
				val intentData = Intent(context, ChatView::class.java)
				intentData.putExtras(
					bundleOf(Pair("id", session.targetUser))
				)

				context.startActivity(intentData)
			}
		}
	}

	override fun getItemCount(): Int = list.size

	fun updateList(newList: List<ChatSession>) {
		notifyItemRangeRemoved(0, list.size)
		list = newList
		notifyItemRangeInserted(0, newList.size)
	}
}