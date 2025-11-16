package remote.api_usecase

import remote.api_interface.IChatApiService
import remote.IEndpointCaller
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import remote.RemoteParams


class CreateConversationsUsecase(
    private val chatApiService: IChatApiService
): IEndpointCaller<CreateConversationsParams, ParentResponse<String>, IResponse<ParentResponse<String>>> {
    override suspend fun call(
        params: CreateConversationsParams,
        handler: IResponseHandler<ParentResponse<String>, IResponse<ParentResponse<String>>>?
    ) {
        handler?.onResponse(chatApiService.createConversations(params))
    }

}

data class CreateConversationsParams(
    override val request: Request,
    val token: String
): RemoteParams(request = request) {

    data class Request(
        val username: String
    )
}