package repository

import ChatServiceErrorResponse
import model.Message
import listeners.ConversationEventListener
import remote.api_usecase.AddNewChatUsecase
import remote.api_usecase.CreateConversationsParams
import remote.api_usecase.CreateConversationsUsecase
import remote.api_usecase.StartNewChatParams
import model.response.NewChatResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import listeners.ChatEngineEventListener
import okhttp3.Response
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import utils.newAppWideChatService

class ConversationsRepository(
    private val token: String,
    private val username: String,
    private val addNewChatUsecase: AddNewChatUsecase,
    private val createConversationsUsecase: CreateConversationsUsecase
): ChatEngineEventListener<Message> {

    private val chatEngine = newAppWideChatService(username, this)

    val context = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var conversationEventListener: ConversationEventListener? = null

    fun setListener(listener: ConversationEventListener) {
        conversationEventListener = listener
    }

    suspend fun createNewConversations(username: String, onSuccess: (() -> Unit)) {
        val params = CreateConversationsParams(
            token = token,
            request = CreateConversationsParams.Request(
                username = username
            )
        )
        createConversationsUsecase.call(
            params = params, object: IResponseHandler<ParentResponse<String>, IResponse<ParentResponse<String>>> {
                override fun onResponse(response: IResponse<ParentResponse<String>>) {
                    when(response) {
                        is IResponse.Success -> {
                            onSuccess()
                        }
                        is IResponse.Failure -> {
                            context.launch(Dispatchers.Main) {
                                conversationEventListener?.onError(response)
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        )
    }

    private suspend fun addNewChat(
        username: String, otherUser: String,
        completion: (chatRef: String?, isSuccess: Boolean) -> Unit) {
        val params = StartNewChatParams(
            token = token,
            request = StartNewChatParams.Request(
                user = username, other = otherUser
            )
        )
        addNewChatUsecase.call(
            params, object:
                IResponseHandler<ParentResponse<NewChatResponse>, IResponse<ParentResponse<NewChatResponse>>> {
            override fun onResponse(response: IResponse<ParentResponse<NewChatResponse>>) {
               when (response) {
                   is IResponse.Success -> {
                       response.data.data?.let {
                           completion(it.chatReference, true)
                       }
                   }
                   is IResponse.Failure -> {
                       context.launch(Dispatchers.Main) {
                           completion(null,false)
                           conversationEventListener?.onAddNewChatFailed(response)
                       }
                   }

                   else -> {

                   }
               }
            }
        })
    }

    override fun onClose(code: Int, reason: String) {
        conversationEventListener?.onClose(code, reason)
    }

    override fun onConnect() {
        //userPresenceHelper.postNewUserPresence(PresenceStatus.AWAY)
        conversationEventListener?.onConnect()
    }

    override fun onDisconnect(t: Throwable, response: Response?) {
        conversationEventListener?.onDisconnect(t, response)
    }

    override fun onError(response: ChatServiceErrorResponse) {
        conversationEventListener?.onError(response)
    }

    override fun onReceive(message: Message) {
        /*PresenceStatus.getType(message.presenceStatus)?.let {
            context.launch(Dispatchers.Main) {
                userPresenceHelper.handlePresenceMessage(
                    it, message.id, message.chatReference
                ) {}
            }
            return
        }

        MessageStatus.getType(message.messageStatus)?.let {
            conversationEventListener?.onReceiveRecipientMessageStatus(message.chatReference, it)
            return
        }*/
    }

    override fun onSent(message: Message) {

    }

    suspend fun connectToChatService(username: String) {
        createNewConversations(username) {
            chatEngine.connect()
        }
    }

    suspend fun addNewConversation(username: String, other: String, completion: (chatRef: String) -> Unit) {
        addNewChat(username, other) { chatRef, isAdded ->
            if (isAdded) {
                context.launch(Dispatchers.IO) {
                    completion(chatRef!!)
                }
            }
        }
    }
}