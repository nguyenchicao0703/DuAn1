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
import kotlin.collections.ArrayList

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

        // bindings
        findViewById<ImageView>(R.id.chat_iv_back).setOnClickListener { finish() }
        messageBox = findViewById(R.id.chat_edt_messenger)
        messageRecycler = findViewById(R.id.chat_recycler_content)

        // message box handler
        findViewById<ImageView>(R.id.chat_iv_send).setOnClickListener(messageSendListener)

        // get target user
        controllerCustomer.getAsync(intent?.extras?.getString("id", null),
        successListener = object : ControllerBase.SuccessListener() {
            override fun run(dataSnapshot: DataSnapshot?) {
                val targetUser = dataSnapshot?.getValue(Customer::class.java)!!

                // load session
                loadChatSession(targetUser)

                // register listener
                registerChatListener()
            }
        },
        failureListener = object : ControllerBase.FailureListener() {
            override fun run(error: Exception?) {
                Toast.makeText(this@ChatView, "User not found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadChatSession(targetUser: Customer) {
        controllerChatSession.getAsync("${SessionUser.sessionId}_${targetUser.id}",
        successListener = object : ControllerBase.SuccessListener() {
            override fun run(dataSnapshot: DataSnapshot?) {
                chatSession = dataSnapshot?.getValue(ChatSession::class.java)

                // if session is null, create one
                if (chatSession == null) {
                    chatSession = ChatSession(
                        null,
                        SessionUser.sessionId,
                        targetUser.id,
                        mutableListOf()
                    )
                    controllerChatSession.setSync(chatSession!!, false)
                }

                messageRecycler.let {
                    messageRecyclerAdapter = ChatViewAdapter(this@ChatView,
                        chatSession!!.messages ?: ArrayList())
                    it.adapter = messageRecyclerAdapter
                }
            }
        },
        failureListener = object : ControllerBase.FailureListener() {
            override fun run(error: Exception?) {
                Toast.makeText(this@ChatView, "Failed to load chat session", Toast.LENGTH_SHORT)
                    .show()
            }
        })
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
                    messageRecyclerAdapter.updateList(chatSession!!.messages!!)
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
            chatSession!!.messages!!.add(
                ChatMessage(
                    SessionUser.sessionId,
                    messageBox.text.toString(),
                    Date().time.toString()
                )
            )

            // update the chat session asynchronously
            controllerChatSession.setAsync(
                chatSession!!, true,
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run() {
                        Log.i(this@ChatView::class.simpleName, "Updated chat session")
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
