package com.fpoly.project1.firebase.model;

import java.util.List;

public class ChatSession {
    public String __id;
    public String userA;
    public String userB;
    public List<ChatMessage> messages;

    public ChatSession() {}

    public ChatSession(String __id, String userA, String userB, List<ChatMessage> messages) {
        this.__id = __id;
        this.userA = userA;
        this.userB = userB;
        this.messages = messages;
    }

    public static class ChatMessage {
        public String senderId;
        public String messageContent;
        public String sentDate;

        public ChatMessage() {}

        public ChatMessage(String senderId, String messageContent, String sentDate) {
            this.senderId = senderId;
            this.messageContent = messageContent;
            this.sentDate = sentDate;
        }
    }
}
