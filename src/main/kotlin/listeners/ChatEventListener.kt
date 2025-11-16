package listeners

import ChatServiceErrorResponse
import model.MessageStatus
import model.PresenceStatus
import model.Message
import okhttp3.Response

interface ChatEventListener: SocketConnection, MessageSender, MessageReceiver {
    fun onReceiveRecipientActivityStatusMessage(presenceStatus: PresenceStatus)
    fun onMessageSent(message: Message)
}

interface SocketConnection {
    fun onClose(code: Int, reason: String)
    fun onConnect()
    fun onDisconnect(t: Throwable, response: Response?)
    fun onError(error: ChatServiceErrorResponse)
}

interface MessageSender {
    fun onSend(message: Message) {}
}

interface MessageReceiver {
    fun onReceiveRecipientMessageStatus(chatRef: String, messageStatus: MessageStatus)
    fun onReceive(message: Message)
}