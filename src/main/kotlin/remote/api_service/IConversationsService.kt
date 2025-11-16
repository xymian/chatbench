package remote.api_service

import remote.api_usecase.StartNewChatParams
import model.response.NewChatResponse
import remote.IResponse
import remote.ParentResponse


interface IConversationsService {

    suspend fun addNewConversation(params: StartNewChatParams): IResponse<ParentResponse<NewChatResponse>>
}