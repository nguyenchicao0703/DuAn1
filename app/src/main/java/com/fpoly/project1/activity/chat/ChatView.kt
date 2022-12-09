package com.fpoly.project1.activity.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.adapter.ChatViewAdapter
import com.fpoly.project1.firebase.Firebase
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
import java.time.Instant

class ChatView : AppCompatActivity() {
    private lateinit var messageBox: EditText
    private lateinit var messageRecycler: RecyclerView
    private lateinit var messageRecyclerAdapter: ChatViewAdapter
    private var chatSession: ChatSession? = null
    private val controllerCustomer = ControllerCustomer()
    private val controllerChatSession = ControllerChatSession()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_session)

        // bindings
        findViewById<ImageView>(R.id.chat_iv_back).setOnClickListener { finish() }
        messageBox = findViewById(R.id.chat_edt_messenger)
        messageRecycler = findViewById(R.id.chat_recycler_content)

        // message box handler
        findViewById<ImageView>(R.id.chat_iv_send).setOnClickListener(messageSendListener)

        // get target user
        if (intent.extras?.getString("id", null) == SessionUser.sessionId) {
            Toast.makeText(this, "Trying to chat yourself", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            controllerCustomer.getAsync(intent.extras?.getString("id", null),
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        val targetUser = dataSnapshot?.getValue(Customer::class.java)!!

                        findViewById<TextView>(R.id.chat_txt_name).text = targetUser.fullName
                        Firebase.storage.child("/avatars/${targetUser.id}.jpg").downloadUrl
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful)
                                    Glide.with(this@ChatView).load(task.result)
                                        .into(findViewById(R.id.chat_iv_avata))
                                else
                                    Glide.with(this@ChatView).load(targetUser.avatarUrl)
                                        .into(findViewById(R.id.chat_iv_avata))
                            }

                        // load session
                        loadChatSession(targetUser)
                    }
                },
                failureListener = object : ControllerBase.FailureListener() {
                    override fun run(error: Exception?) {
                        Toast.makeText(this@ChatView, "User not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })
        }
    }

    private fun loadChatSession(targetUser: Customer) {
        controllerChatSession.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val sessions = java.util.ArrayList<ChatSession>()
                    dataSnapshot?.children?.forEach { session ->
                        sessions.add(session.getValue(ChatSession::class.java)!!)
                    }

                    val matchingSession = sessions.filter {
                        it.id!!.contains(SessionUser.sessionId!!) &&
                                it.id!!.contains(targetUser.id!!)
                    }
                    chatSession = if (matchingSession.isNotEmpty()) matchingSession[0] else null

                    if (chatSession == null) {
                        newChatSession(targetUser)
                    } else {
                        // register listener
                        registerChatListener()

                        messageRecycler.let {
                            messageRecyclerAdapter = ChatViewAdapter(
                                this@ChatView,
                                chatSession!!.messages ?: ArrayList()
                            )
                            it.adapter = messageRecyclerAdapter
                        }
                    }
                }
            },
            failureListener = object : ControllerBase.FailureListener() {
                override fun run(error: Exception?) {
                    newChatSession(targetUser)
                }
            })
    }

    private fun newChatSession(targetUser: Customer) {
        chatSession =
            ChatSession(
                "${SessionUser.sessionId}_${targetUser.id}",
                targetUser = targetUser.id,
                sendingUser = SessionUser.sessionId,
                messages = ArrayList()
            )

        controllerChatSession.setAsync(chatSession!!,
            false,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run() {
                    // register listener
                    registerChatListener()

                    messageRecycler.let {
                        messageRecyclerAdapter = ChatViewAdapter(
                            this@ChatView,
                            chatSession!!.messages!!
                        )
                        it.adapter = messageRecyclerAdapter
                    }
                }
            },
            failureListener = object : ControllerBase.FailureListener() {
                override fun run(error: Exception?) {
                    Toast.makeText(
                        this@ChatView, "Failed to load session", Toast
                            .LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun registerChatListener() {
        // value change listener
        controllerChatSession.registerValueChangeListener(
            chatSession!!,
            object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // override current chat session
                    chatSession = snapshot.getValue(ChatSession::class.java)

                    // update recycler list
                    messageRecyclerAdapter.updateList(
                        chatSession!!.messages ?: ArrayList()
                    )

                    // scroll to bottom
                    messageRecycler.scrollToPosition(messageRecyclerAdapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(this@ChatView::class.simpleName, "Failed to fetch chat messages")
                    println(error)
                }
            }
        )
    }

    private val messageSendListener =
        View.OnClickListener { // create new message object and add it to session
            if (chatSession!!.messages == null)
                chatSession!!.messages = ArrayList()

            chatSession!!.messages!!.add(
                ChatMessage(
                    SessionUser.sessionId,
                    messageBox.text.toString(),
                    Instant.now().toEpochMilli()
                )
            )

            // update the chat session asynchronously
            controllerChatSession.setAsync(
                chatSession!!, true,
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run() {
                        // empty box
                        messageBox.setText("")

                        // update recycler list
                        messageRecyclerAdapter.updateList(
                            chatSession!!.messages ?: ArrayList()
                        )

                        // scroll to bottom
                        messageRecycler.scrollToPosition(messageRecyclerAdapter.itemCount - 1)
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
