package org.example

import chat.models.ChatInfo

interface IChatUser {
    var id: String
    var token: String?
    var activeChat: ChatInfo?
    fun signUp(username: String, password: String)
    fun login(username: String, password: String)
    fun connectToChat()
    fun connectWith(username: String)
    fun sendMessage(text: String)
    fun sentMessageCount()
    fun missingMessageCount()
}