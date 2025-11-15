package chat.repository

import ChatEngine
import ChatServiceErrorResponse
import chat.IChatStorage
import chat.interfaces.ChatEventListener
import chat.remote.models.Message
import chat.models.ChatInfo
import chat.models.MessageStatus
import chat.remote.api_usecases.CreateChatRoomParams
import chat.remote.api_usecases.CreateChatRoomUsecase
import utils.toISOString
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import listeners.ChatEngineEventListener
import okhttp3.Response
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import java.time.LocalDateTime
import java.util.UUID

class ChatRepository(
    private val chatInfo: ChatInfo,
    private val createChatRoomUsecase: CreateChatRoomUsecase,
    private val chatDb: IChatStorage,
    private val chatEngine: ChatEngine<Message>
): ChatEngineEventListener<Message> {

    private val context = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var chatEventListener: ChatEventListener? = null
    private var chatService = chatEngine

    fun connectAndSendPendingMessages() {
        /*context.launch(Dispatchers.IO) {
            val pendingMessages = mutableListOf<Message>()
            pendingMessages.addAll(chatDb.getUndeliveredMessages(
                chatInfo.username, chatInfo.chatReference).toMessages()
            )
            pendingMessages.addAll(
                chatDb.getPendingMessages(chatInfo.chatReference).toMessages()
            )
            context.launch(Dispatchers.Main) {
                createNewChatRoom {
                    chatService.connectAndSend(pendingMessages)
                }
            }
        }*/
    }

    fun killChatService() {
        chatService.disconnect()
        chatService = ChatEngine.Builder<Message>()
            .build(Message.serializer())
    }

    fun setChatEventListener(listener: ChatEventListener) {
        chatEventListener = listener
    }

    fun markMessagesAsSeen(message: Message) {
        /*if (message.timestamp > cutOffForMarkingMessagesAsSeen!!) {
            message.seenTimestamp = LocalDateTime.now().toISOString()
            chatService.returnMessage(message)
        }*/
    }

    fun sendMessage(message: Message) {
        /*context.launch(Dispatchers.IO) {
            chatDb.store(message)
            updateConversationLastMessage(message)
        }*/
        chatService.sendMessage(message)
    }

    fun postMessageStatus(messageStatus: MessageStatus) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            message = "",
            sender = chatInfo.username,
            receiver = chatInfo.recipientsUsernames[0],
            timestamp = LocalDateTime.now().toISOString(),
            chatReference = chatInfo.chatReference,
            messageStatus = messageStatus.name
        )
        chatService.sendMessage(message)
    }

    private suspend fun createNewChatRoom(onSuccess: (() -> Unit)) {
        val params = CreateChatRoomParams(
            request = CreateChatRoomParams.Request(
                user = chatInfo.username,
                other = chatInfo.recipientsUsernames[0],
                chatReference = chatInfo.chatReference
            )
        )
        createChatRoomUsecase.call(
            params = params, object: IResponseHandler<ParentResponse<String>, IResponse<ParentResponse<String>>> {
                override fun onResponse(response: IResponse<ParentResponse<String>>) {
                    when(response) {
                        is IResponse.Success -> {
                            onSuccess()
                        }
                        is IResponse.Failure -> {
                        }
                        else -> {

                        }
                    }
                }
            }
        )
    }

    override fun onClose(code: Int, reason: String) {
        chatEventListener?.onClose(code, reason)
    }

    override fun onConnect() {
        /*userPresenceHelper.postNewUserPresence(PresenceStatus.ONLINE)*/
        chatEventListener?.onConnect()
    }

    override fun onDisconnect(t: Throwable, response: Response?) {
        when {
            response?.code == HttpStatusCode.NotFound.value -> {
                context.launch(Dispatchers.IO) {
                    createNewChatRoom {
                        chatService.connect()
                    }
                }
            } else -> {
                chatEventListener?.onDisconnect(t, response)
            }
        }
    }

    override fun onError(response: ChatServiceErrorResponse) {
        chatEventListener?.onError(response)
    }

    override fun onSent(message: Message) {
        when {
            message.presenceStatus.isNullOrEmpty() && message.messageStatus.isNullOrEmpty() -> {
                context.launch(Dispatchers.IO) {
                    chatDb.store(message)
                    val dbMessage = message
                    chatDb.setAsSent((dbMessage.id to dbMessage.chatReference))
                }
                chatEventListener?.onMessageSent(message)
            }

            /*!message.presenceStatus.isNullOrEmpty() -> {
                //userPresenceHelper.onPresenceSent(message.id)
            }

            !message.messageStatus.isNullOrEmpty() -> {
            }*/
        }
    }

    private var lastMessagesFromRecipient = mutableListOf<Message>()

    override fun onReceive(message: Message) {
        /*PresenceStatus.getType(message.presenceStatus)?.let {
            context.launch(Dispatchers.Main) {
                userPresenceHelper.handlePresenceMessage(
                    it, message.id, message.chatReference
                ) { status ->
                    chatEventListener?.onReceiveRecipientActivityStatusMessage(status)
                }
            }
            return
        }

        MessageStatus.getType(message.messageStatus)?.let {
            context.launch(Dispatchers.Main) {
                chatEventListener?.onReceiveRecipientMessageStatus(message.chatReference,it)
            }
            return
        }*/

        context.launch(Dispatchers.IO) {
            chatDb.store(message)
        }
        setDeliveredTimestampForMessage(message)
        chatEventListener?.onReceive(message)
    }

    private fun setDeliveredTimestampForMessage(message: Message) {
        val lastMessageOfTheSameId = lastMessagesFromRecipient.find { it.id == message.id }
        if (lastMessageOfTheSameId == null) {
            message.deliveredTimestamp = LocalDateTime.now().toISOString()
            lastMessagesFromRecipient.add(message)
        } else {
            if (message.deliveredTimestamp.isNullOrEmpty()) {
                message.deliveredTimestamp = lastMessageOfTheSameId.deliveredTimestamp
                lastMessagesFromRecipient.remove(lastMessageOfTheSameId)
            }
        }
    }

    fun isChatServiceConnected(): Boolean {
        return chatService.socketIsConnected
    }

    fun cancel() {
        context.cancel()
    }

    fun buildUnsentMessage(message: String): Message {
        return Message(
            id = UUID.randomUUID().toString(),
            message = message,
            sender = chatInfo.username,
            receiver = chatInfo.recipientsUsernames[0],
            timestamp = LocalDateTime.now().toISOString(),
            chatReference = chatInfo.chatReference,
            isReadReceiptEnabled = true
        )
    }
}