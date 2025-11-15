package auth.repository

import auth.remote.api_usecases.LoginParams
import auth.remote.api_usecases.LoginUsecase
import auth.remote.models.LoginResponse
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import remote.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginRepository(
    private val loginUsecase: LoginUsecase,
) {
    private var loginEventListener: LoginEventListener? = null

    private val context = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun login(username: String, password: String) {
        val loginParams = LoginParams(
            request = LoginParams.Request(
                username = username,
                password = password
            )
        )
        context.launch(Dispatchers.Default) {
            loginUsecase.call(
                loginParams, object: IResponseHandler<ParentResponse<LoginResponse>,
                        IResponse<ParentResponse<LoginResponse>>> {
                    override fun onResponse(response: IResponse<ParentResponse<LoginResponse>>) {
                        when (response) {
                            is IResponse.Success -> {
                                context.launch(Dispatchers.Main) {
                                    response.data.data?.let {
                                        context.launch(Dispatchers.IO) {
                                            context.launch(Dispatchers.Main) {
                                                loginEventListener?.onLogin(it)
                                            }
                                        }
                                    }
                                }
                            }
                            is IResponse.Failure -> {
                                context.launch(Dispatchers.Main) {
                                    loginEventListener?.onLoginFailed(response)
                                }
                            }

                            is Response -> {}
                        }
                    }
                }
            )
        }
    }

    fun setEventListener(listener: LoginEventListener) {
        loginEventListener = listener
    }

    fun cancel() {
        context.cancel()
    }
}

interface LoginEventListener {
    fun onLogin(loginInfo: LoginResponse)
    fun onLoginFailed(errorResponse: IResponse.Failure<ParentResponse<LoginResponse>>)
}