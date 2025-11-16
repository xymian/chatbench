package remote.api_service

import remote.api_interface.IAuthApiService
import remote.api_usecase.LoginParams
import remote.api_usecase.SignupParams
import model.response.LoginResponse
import model.response.SignupResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import remote.IResponse
import remote.ParentResponse
import remote.Response

class AuthApiService(private val client: HttpClient): IAuthApiService {

    override suspend fun login(params: LoginParams): IResponse<ParentResponse<LoginResponse>> {
        return Response<ParentResponse<LoginResponse>> {
            client.post("http://192.168.0.2:50051" + "/login") {
                contentType(ContentType.Application.Json)
                setBody(params.request)
            }
        }.invoke()
    }

    override suspend fun signup(params: SignupParams): IResponse<ParentResponse<String>> {
        return Response<ParentResponse<SignupResponse>> {
            client.post("http://192.168.0.2:50051" + "/register") {
                contentType(ContentType.Application.Json)
                setBody(params.request)
            }
        }.invoke()
    }
}