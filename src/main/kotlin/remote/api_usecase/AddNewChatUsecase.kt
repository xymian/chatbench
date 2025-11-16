package remote.api_usecase

import remote.api_service.IConversationsService
import model.response.NewChatResponse
import kotlinx.serialization.Serializable
import remote.IEndpointCaller
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import remote.RemoteParams

class AddNewChatUsecase(
    private val conversationsApiService: IConversationsService
): IEndpointCaller<StartNewChatParams, ParentResponse<NewChatResponse>, IResponse<ParentResponse<NewChatResponse>>> {

    override suspend fun call(
        params: StartNewChatParams,
        handler: IResponseHandler<ParentResponse<NewChatResponse>, IResponse<ParentResponse<NewChatResponse>>>?
    ) {
        handler?.onResponse(conversationsApiService.addNewConversation(params))
    }

}

data class StartNewChatParams(
    override val request: Request,
    val token: String
): RemoteParams(request = request) {

    @Serializable
    class Request(
        val user: String,
        val other: String,
    )
}

