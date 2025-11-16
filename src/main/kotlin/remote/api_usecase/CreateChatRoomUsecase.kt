package remote.api_usecase

import remote.api_interface.IChatApiService
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import remote.IEndpointCaller
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import remote.RemoteParams

class CreateChatRoomUsecase(
    private val chatApiService: IChatApiService
): IEndpointCaller<CreateChatRoomParams, ParentResponse<String>, IResponse<ParentResponse<String>>> {
    override suspend fun call(
        params: CreateChatRoomParams,
        handler: IResponseHandler<ParentResponse<String>, IResponse<ParentResponse<String>>>?
    ) {
        handler?.onResponse(chatApiService.createChatRoom(params))
    }
}

data class CreateChatRoomParams(
    override val request: Request,
    val token: String
): RemoteParams(request = request) {

    @Serializable
    data class Request(
        @SerialName("user")
        val user: String,
        @SerialName("other")
        val other: String,
        @SerialName("chatReference")
        val chatReference: String
    )
}