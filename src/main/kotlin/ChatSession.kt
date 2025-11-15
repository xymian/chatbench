package org.example

import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random

class ChatSession() {

    private var users = mutableListOf<ChatUser>()

    fun addUser(user: ChatUser) {
        users.add(user)
    }

    fun signUpUsers() {
        val tempUsers: Queue<ChatUser> = LinkedList()
        tempUsers.addAll(users)
        while (tempUsers.isNotEmpty()) {
            tempUsers.poll().apply {
                this.signUp(this.id, this.id)
            }
        }
    }

    fun connectUsers() {
        val maxConnectionsPerUser = users.size / 2
        var randomNumber = Random.nextInt(0, maxConnectionsPerUser)
        users.forEach {
            for (i in 0..<randomNumber) {
                var otherUser = users[Random.nextInt(0, users.size - 1)]
                while (it.id == otherUser.id) {
                    otherUser = users[Random.nextInt(0, users.size - 1)]
                }
                it.connectWith(otherUser.id)
            }
            randomNumber = Random.nextInt(0, maxConnectionsPerUser)
        }
    }
}