package model.response

import model.Message
import kotlinx.serialization.Serializable
import models.MessagesResponse

@Serializable
data class ChatHistoryResponse(
    override val data: List<Message>? = listOf(),
    override val isSuccessful: Boolean? = false,
    override val message: String?
): MessagesResponse<Message>()