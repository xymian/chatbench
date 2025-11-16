import model.response.LoginResponse
import repository.LoginEventListener
import repository.LoginRepository
import repository.SignupEventListener
import repository.SignupRepository
import model.ChatInfo
import remote.api_service.ChatApiService
import remote.api_usecase.CreateChatRoomUsecase
import model.Message
import repository.ChatRepository
import remote.api_service.ConversationsService
import remote.api_usecase.AddNewChatUsecase
import remote.api_usecase.CreateConversationsUsecase
import repository.ConversationsRepository
import remote.IResponse
import remote.ParentResponse
import remote.client
import utils.toISOString
import java.time.LocalDateTime
import java.util.UUID

class ChatUser(
    val username: String,
    val password: String,
    private val loginRepository: LoginRepository,
    private val signupRepository: SignupRepository,
): LoginEventListener, SignupEventListener {

    private var token: String? = null

    private var activeChats = mutableMapOf<String, ChatRepository>()


    fun signUp() {
        signupRepository.signUp(username, password)
    }

    fun login() {
        loginRepository.login(username, password)
    }

    suspend fun connectWith(username: String) {
        val conversationRepository = ConversationsRepository(
            token = token ?: "",
            username = username,
            AddNewChatUsecase(ConversationsService(client)),
            CreateConversationsUsecase(ChatApiService(client))
        )
        conversationRepository.addNewConversation(
            this.username, username
        ) { chatRef ->
            val chatInfo = ChatInfo(
                username = this.username,
                recipientsUsernames = listOf(username),
                chatReference = chatRef
            )
            val chatRepository = ChatRepository(
                chatInfo,
                token = token ?: "",
                CreateChatRoomUsecase(ChatApiService(client))
            )
            activeChats[username] = chatRepository
            chatRepository.connect()
        }
    }

    fun sendMessage(text: String, to: String) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            message = text,
            sender = username,
            receiver = to,
            timestamp = LocalDateTime.now().toISOString(),
            chatReference = activeChats[to]?.chatInfo?.chatReference!!,
            isReadReceiptEnabled = false
        )
        activeChats[to]?.sendMessage(message)
    }

    fun sentMessageCount() {
    }

    fun missingMessageCount() {
    }

    override fun onLogin(loginInfo: LoginResponse) {
        token = loginInfo.accessToken
        println("$username has logged in")
    }

    override fun onLoginFailed(errorResponse: IResponse.Failure<ParentResponse<LoginResponse>>) {
        println("$username login has failed")
    }

    override fun onSignUp() {
        println("$username has signed up")
        login()
    }

    override fun onSignUpFailed( errorResponse: IResponse.Failure<ParentResponse<String>>) {
        println("$username signup has failed")
    }
}