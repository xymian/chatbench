package listeners

import model.response.NewChatResponse
import remote.IResponse
import remote.ParentResponse

interface ConversationEventListener: SocketConnection, MessageReceiver, MessageSender {
    fun onAddNewChatFailed(error: IResponse.Failure<ParentResponse<NewChatResponse>>)
    fun onNewChatAdded(chat: NewChatResponse)
    fun onError(response: IResponse.Failure<ParentResponse<String>>)
}