package auth.remote.api_services

import auth.remote.api_usecases.LoginParams
import auth.remote.models.LoginResponse
import auth.remote.models.SignupResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.cio.Response
import io.ktor.http.contentType

class AuthApiService(private val client: HttpClient): IAuthApiService {

    override suspend fun login(params: LoginParams): IResponse<ParentResponse<LoginResponse>> {
        return Response<ParentResponse<LoginResponse>> {
            client.post(BuildConfig.AUTH_BASE_URL + "/login") {
                contentType(ContentType.Application.Json)
                setBody(params.request)
            }
        }.invoke()
    }

    override suspend fun signup(params: SignupParams): IResponse<ParentResponse<String>> {
        return Response<ParentResponse<SignupResponse>> {
            client.post(BuildConfig.AUTH_BASE_URL + "/register") {
                contentType(ContentType.Application.Json)
                setBody(params.request)
            }
        }.invoke()
    }
}