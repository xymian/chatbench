package com.simulatedtez.gochat.chat.remote.models

import chat.remote.models.Message
import kotlinx.serialization.Serializable
import models.MessagesResponse

@Serializable
data class ChatHistoryResponse(
    override val data: List<Message>? = listOf(),
    override val isSuccessful: Boolean? = false,
    override val message: String?
): MessagesResponse<Message>()
