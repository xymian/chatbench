package chat

import chat.remote.models.Message

interface IChatStorage {
    suspend fun store(messages: List<Message>)
    suspend fun store(message: Message)
    suspend fun deleteAllMessages()
    suspend fun getUndeliveredMessages(username: String, chatRef: String): List<Message>
    suspend fun setAsSeen(vararg messageRefToChatRef: Pair<String, String>)
    suspend fun setAsSent(vararg messageRefToChatRef: Pair<String, String>)
    suspend fun getMessage(messageRef: String): Message?
    suspend fun getPendingMessages(chatRef: String): List<Message>
    suspend fun isEmpty(chatRef: String): Boolean
    suspend fun loadNextPage(chatRef: String): List<Message>
}