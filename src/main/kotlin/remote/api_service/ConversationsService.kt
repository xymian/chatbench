package remote.api_service

import remote.api_usecase.StartNewChatParams
import model.response.NewChatResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import remote.IResponse
import remote.ParentResponse
import remote.Response

class ConversationsService(private val client: HttpClient): IConversationsService {
    override suspend fun addNewConversation(params: StartNewChatParams): IResponse<ParentResponse<NewChatResponse>> {
        return Response<ParentResponse<NewChatResponse>> {
            client.post("http://192.168.0.2:50053" + "/chatReference") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${params.token}")
                setBody(params.request)
            }
        }.invoke()
    }
}