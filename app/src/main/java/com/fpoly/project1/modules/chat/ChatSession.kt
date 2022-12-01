package com.fpoly.project1.modules.chatimport

import java.util.HashMap

com.facebook.login.LoginManager.logOut

class ChatSession(
    var _id: String,
    var uid1: String,
    var uid2: String,
    var messages: HashMap<String, String>
)