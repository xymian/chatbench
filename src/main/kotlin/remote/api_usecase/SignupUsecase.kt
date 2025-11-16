package remote.api_usecase

import remote.api_interface.IAuthApiService
import remote.IResponseHandler
import kotlinx.serialization.Serializable
import remote.IEndpointCaller
import remote.IResponse
import remote.ParentResponse
import remote.RemoteParams

class SignupUsecase(
    private val authApiService: IAuthApiService
): IEndpointCaller<SignupParams, ParentResponse<String>, IResponse<ParentResponse<String>>> {

    override suspend fun call(
        params: SignupParams,
        handler: IResponseHandler<ParentResponse<String>, IResponse<ParentResponse<String>>>?
    ) {
        handler?.onResponse(authApiService.signup(params))
    }
}

data class SignupParams(
    override val request: Request
): RemoteParams(null, request) {

    @Serializable
    data class Request(
        val username: String,
        val password: String,
    )
}