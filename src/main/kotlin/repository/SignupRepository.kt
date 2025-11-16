package repository

import remote.api_usecase.SignupParams
import remote.api_usecase.SignupUsecase
import remote.IResponse
import remote.IResponseHandler
import remote.ParentResponse
import remote.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SignupRepository(
    private val signupUsecase: SignupUsecase,
) {
    private val context = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var signupEventListener: SignupEventListener? = null

    fun setEventListener(listener: SignupEventListener) {
        signupEventListener = listener
    }

    fun signUp(username: String, password: String) {
        val signupParams = SignupParams(
            request = SignupParams.Request(
                username = username,
                password = password
            )
        )
        context.launch(Dispatchers.Default) {
            signupUsecase.call(
                signupParams, object: IResponseHandler<ParentResponse<String>,
                        IResponse<ParentResponse<String>>> {
                    override fun onResponse(response: IResponse<ParentResponse<String>>) {
                        when (response) {
                            is IResponse.Success -> {
                                response.data.data?.let {
                                    signupEventListener?.onSignUp()
                                }
                            }
                            is IResponse.Failure -> {
                                signupEventListener?.onSignUpFailed(response)
                            }

                            is Response -> {}
                        }
                    }
                }
            )
        }
    }

    fun cancel() {
        context.cancel()
    }
}

interface SignupEventListener {
    fun onSignUp()
    fun onSignUpFailed(errorResponse: IResponse.Failure<ParentResponse<String>>)
}