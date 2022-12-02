package com.fpoly.project1.firebase.model

data class ChatSession(
    var __id: String? = null,
    var sendingUser: String? = null,
    var targetUser: String? = null,
    var messages: MutableList<ChatMessage>? = null
)

data class ChatMessage(
    var senderId: String? = null,
    var messageContent: String? = null,
    var sentDate: String? = null
)
