package remote.api_interface

import remote.IResponse
import remote.ParentResponse
import remote.api_usecase.CreateChatRoomParams
import remote.api_usecase.CreateConversationsParams

interface IChatApiService {
    suspend fun createChatRoom(params: CreateChatRoomParams): IResponse<ParentResponse<String>>
    suspend fun createConversations(params: CreateConversationsParams): IResponse<ParentResponse<String>>
}