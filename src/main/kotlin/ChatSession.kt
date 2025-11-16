import remote.api_service.AuthApiService
import remote.api_usecase.LoginUsecase
import remote.api_usecase.SignupUsecase
import repository.LoginRepository
import repository.SignupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import remote.client
import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random

class ChatSession(numberOfUsers: Int) {

    private var users = mutableListOf<ChatUser>()

    init {
        repeat(numberOfUsers) {
            users.add(
                ChatUser(
                    "user $it", password = "user $it",
                    LoginRepository(
                        loginUsecase = LoginUsecase(AuthApiService(client))
                    ),
                    SignupRepository(
                        signupUsecase = SignupUsecase(AuthApiService(client))
                    )
                )
            )
        }
    }

    fun signUpUsers() {
        val tempUsers: Queue<ChatUser> = LinkedList()
        tempUsers.addAll(users)
        while (tempUsers.isNotEmpty()) {
            tempUsers.poll().apply {
                this.signUp()
            }
        }
    }

    val context = CoroutineScope(Dispatchers.Default + Job())

    fun connectUsers() {
        val maxConnectionsPerUser = users.size / 2
        var randomNumber = Random.nextInt(0, maxConnectionsPerUser)
        users.forEach { user ->
            repeat(randomNumber) {
                var otherUser = users[Random.nextInt(0, users.size - 1)]
                while (user.username == otherUser.username) {
                    otherUser = users[Random.nextInt(0, users.size - 1)]
                }
                context.launch(Dispatchers.IO) {
                    user.connectWith(otherUser.username)
                }
            }
            randomNumber = Random.nextInt(0, maxConnectionsPerUser)
        }
    }
}