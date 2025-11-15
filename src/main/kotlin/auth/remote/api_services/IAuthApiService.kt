package auth.remote.api_services

import auth.remote.api_usecases.LoginParams
import auth.remote.models.LoginResponse

interface IAuthApiService {
    suspend fun login(params: LoginParams): IResponse<ParentResponse<LoginResponse>>
    suspend fun signup(params: SignupParams): IResponse<ParentResponse<String>>
}