package com.fpoly.project1.activity.chat

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.adapter.ChatViewAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerChatSession
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.model.ChatMessage
import com.fpoly.project1.firebase.model.ChatSession
import com.fpoly.project1.firebase.model.Customer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class ChatView : AppCompatActivity() {
	private lateinit var messageBox: EditText
	private lateinit var messageRecycler: RecyclerView
	private lateinit var messageRecyclerAdapter: ChatViewAdapter
	private var chatSession: ChatSession? = null
	private val controllerCustomer = ControllerCustomer()
	private val controllerChatSession = ControllerChatSession()

	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
		setContentView(R.layout.chat_session)

		// get target user
		val targetUser = controllerCustomer.getSync(intent.extras?.getString("id", null))
		if (targetUser == null) {
			Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
			return
		}

		// load session
		loadChatSession(targetUser)

		// register listener
		registerChatListener()

		// bindings
		findViewById<ImageView>(R.id.chat_iv_back).setOnClickListener { finish() }
		messageBox = findViewById(R.id.chat_edt_messenger)
		messageRecycler = findViewById(R.id.chat_recycler_content)
		messageRecycler.let {
			messageRecyclerAdapter = ChatViewAdapter(this, chatSession!!.messages!!)
			it.adapter = messageRecyclerAdapter
		}

		// message box handler
		findViewById<ImageView>(R.id.chat_iv_send).setOnClickListener(messageSendListener)
	}

	private fun loadChatSession(targetUser: Customer) {
		// get session
		chatSession =
			controllerChatSession.getSync("${SessionUser.sessionId}_${targetUser.__id}")
				?: controllerChatSession.getSync("${targetUser.__id}_${SessionUser.sessionId}")

		// if session is null, create one
		if (chatSession == null) {
			chatSession = ChatSession(
				null,
				SessionUser.sessionId,
				targetUser.__id,
				mutableListOf()
			)
			controllerChatSession.setSync(chatSession!!, false)
		}
	}

	private fun registerChatListener() {
		// value change listener
		controllerChatSession.registerValueChangeListener(chatSession!!, object :
			ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				// override current chat session
				chatSession = snapshot.getValue(ChatSession::class.java)

				// update recycler list
				messageRecyclerAdapter.updateList(chatSession!!.messages!!)
			}

			override fun onCancelled(error: DatabaseError) {
				Log.e(this@ChatView::class.simpleName, "Failed to fetch chat messages")
				println(error)
			}
		})
	}

	private val messageSendListener =
		View.OnClickListener { // create new message object and add it to session
			chatSession!!.messages!!.add(
				ChatMessage(
					SessionUser.sessionId,
					messageBox.text.toString(),
					Date().time.toString()
				)
			)

			// update the chat session asynchronously
			controllerChatSession.setAsync(chatSession!!, true,
				successListener = object : ControllerBase.SuccessListener() {
					override fun run() {
						Log.i(this@ChatView::class.simpleName, "Updated chat session")
					}

					override fun run(unused: Any?) {
						TODO("Not yet implemented")
					}

					override fun run(dataSnapshot: DataSnapshot?) {
						TODO("Not yet implemented")
					}
				},
				failureListener = object : ControllerBase.FailureListener() {
					override fun run(error: Exception?) {
						Toast.makeText(this@ChatView, "Failed to send message", Toast.LENGTH_SHORT)
							.show()
					}
				}
			)
		}
}