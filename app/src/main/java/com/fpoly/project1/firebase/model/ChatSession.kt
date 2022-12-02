package com.fpoly.project1.firebase.model

data class ChatSession(
    var __id: String? = null,
    var userA: String? = null,
    var userB: String? = null,
    var messages: List<ChatMessage>? = null
)

data class ChatMessage(
    var senderId: String? = null,
    var messageContent: String? = null,
    var sentDate: String? = null
)
