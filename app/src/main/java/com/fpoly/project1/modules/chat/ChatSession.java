package com.fpoly.project1.modules.chat;

import java.util.HashMap;
import java.util.List;

public class ChatSession {
    public String _id;
    public String uid1;
    public String uid2;
    public HashMap<String, String> messages;

    public ChatSession(String _id, String uid1, String uid2, HashMap<String, String> messages) {
        this._id = _id;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.messages = messages;
    }
}
