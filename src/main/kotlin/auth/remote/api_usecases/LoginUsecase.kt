package auth.remote.api_usecases

import auth.remote.api_services.IAuthApiService
import auth.remote.models.LoginResponse
import kotlinx.serialization.Serializable
import org.example.IEndpointCaller
import org.example.RemoteParams

class LoginUsecase(
    private val authApiService: IAuthApiService
): IEndpointCaller<LoginParams, ParentResponse<LoginResponse>, IResponse<ParentResponse<LoginResponse>>> {

    override suspend fun call(
        params: LoginParams,
        handler: IResponseHandler<ParentResponse<LoginResponse>, IResponse<ParentResponse<LoginResponse>>>?
    ) {
        handler?.onResponse(authApiService.login(params))
    }
}

data class LoginParams(
    override val request: Request
): RemoteParams(null, request) {

    @Serializable
    class Request(
        val username: String,
        val password: String,
    )
}