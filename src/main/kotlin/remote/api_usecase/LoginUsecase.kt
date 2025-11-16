package remote.api_usecase

import remote.api_interface.IAuthApiService
import model.response.LoginResponse
import kotlinx.serialization.Serializable
import remote.IEndpointCaller
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import remote.RemoteParams

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