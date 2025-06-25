package com.example.chatapp.domain.usecase


import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.repository.ChatRepository

class SendMessageUseCase(private val repo: ChatRepository) {
    suspend operator fun invoke(message: Message) {
        repo.sendMessage(message)
    }
}
