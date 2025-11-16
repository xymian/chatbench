package remote.api_service

import remote.api_interface.IChatApiService
import remote.api_usecase.CreateChatRoomParams
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import remote.IResponse
import remote.ParentResponse
import remote.Response
import remote.api_usecase.CreateConversationsParams

class ChatApiService(private val client: HttpClient): IChatApiService {

    override suspend fun createChatRoom(params: CreateChatRoomParams): IResponse<ParentResponse<String>> {
        return Response<String> {
            client.post("http://192.168.0.2:50053" + "/chat") {
                header(HttpHeaders.Authorization, "Bearer ${params.token}")
                setBody(params.request)
            }
        }.invoke()
    }

    override suspend fun createConversations(params: CreateConversationsParams): IResponse<ParentResponse<String>> {
        return Response<String> {
            client.post("http://192.168.0.2:50053" + "/interactions/${params.request.username}") {
                header(HttpHeaders.Authorization, "Bearer ${params.token}")
            }
        }.invoke()
    }
}