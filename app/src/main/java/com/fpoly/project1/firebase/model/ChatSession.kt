package com.fpoly.project1.firebase.model

class ChatSession {
    var __id: String? = null
    var userA: String? = null
    var userB: String? = null
    var messages: List<ChatMessage>? = null

    constructor()
    constructor(__id: String?, userA: String?, userB: String?, messages: List<ChatMessage>?) {
        this.__id = __id
        this.userA = userA
        this.userB = userB
        this.messages = messages
    }

    class ChatMessage {
        var senderId: String? = null
        var messageContent: String? = null
        var sentDate: String? = null

        constructor()
        constructor(senderId: String?, messageContent: String?, sentDate: String?) {
            this.senderId = senderId
            this.messageContent = messageContent
            this.sentDate = sentDate
        }
    }
}