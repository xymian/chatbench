package org.example

import auth.remote.models.LoginResponse
import auth.repository.LoginEventListener
import auth.repository.LoginRepository
import auth.repository.SignupEventListener
import auth.repository.SignupRepository
import chat.models.ChatInfo
import chat.repository.ChatRepository
import remote.IResponse
import remote.ParentResponse
import java.util.UUID

class ChatUser(
    private val chatRepository: ChatRepository,
    private val loginRepository: LoginRepository,
    private val signupRepository: SignupRepository
): IChatUser, LoginEventListener, SignupEventListener {

    override var id = UUID.randomUUID().toString()
    override var token: String? = null

    override var activeChat: ChatInfo? = null


    override fun signUp(username: String, password: String) {
        signupRepository.signUp(username, password)
    }

    override fun login(username: String, password: String) {
        loginRepository.login(username, password)
    }

    override fun connectToChat() {
    }

    override fun connectWith(username: String) {

    }

    override fun sendMessage(text: String) {
    }

    override fun sentMessageCount() {
    }

    override fun missingMessageCount() {
    }

    override fun onLogin(loginInfo: LoginResponse) {
        token = loginInfo.accessToken
    }

    override fun onLoginFailed(errorResponse: IResponse.Failure<ParentResponse<LoginResponse>>) {
        //
    }

    override fun onSignUp(username: String, password: String) {
        login(username, password)
    }

    override fun onSignUpFailed(errorResponse: IResponse.Failure<ParentResponse<String>>) {
        //
    }
}