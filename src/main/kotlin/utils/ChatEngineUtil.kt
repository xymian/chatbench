package utils

import ChatEngine
import MessageReturner
import listeners.ChatEngineEventListener
import model.Message
import java.time.LocalDateTime

fun newAppWideChatService(username: String, eventListener: ChatEngineEventListener<Message>): ChatEngine<Message> {
    return ChatEngine.Builder<Message>()
        .setSocketURL(
            "ws://192.168.0.2/conversations/${username}"
        )
        .setUsername(username)
        .setExpectedReceivers(listOf())
        .setChatServiceListener(eventListener)
        .setMessageReturner(socketMessageLabeler(username))
        .build(Message.serializer())
}

fun socketMessageLabeler(username: String): MessageReturner<Message> {
    return object : MessageReturner<Message> {
        override fun returnMessage(
            message: Message
        ): Message {
            return Message(
                id = message.id,
                message = message.message,
                sender = message.sender,
                receiver = message.receiver,
                timestamp = message.timestamp,
                chatReference = message.chatReference,
                deliveredTimestamp = LocalDateTime.now().toISOString(),
                seenTimestamp = message.seenTimestamp,
                isReadReceiptEnabled = message.isReadReceiptEnabled
            )
        }

        override fun isMessageReturnable(message: Message): Boolean {
            return message.sender != username
                    && message.deliveredTimestamp == null
                    && message.presenceStatus.isNullOrEmpty()
                    && message.messageStatus.isNullOrEmpty()
        }
    }
}