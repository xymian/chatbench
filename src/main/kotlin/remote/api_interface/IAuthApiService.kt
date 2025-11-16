package remote.api_interface

import remote.api_usecase.LoginParams
import remote.api_usecase.SignupParams
import model.response.LoginResponse
import remote.IResponse
import remote.ParentResponse

interface IAuthApiService {
    suspend fun login(params: LoginParams): IResponse<ParentResponse<LoginResponse>>
    suspend fun signup(params: SignupParams): IResponse<ParentResponse<String>>
}