package com.example.chatapp.domain.usecase

import com.example.chatapp.domain.repository.ChatRepository

class GetMessagesUseCase(private val repo: ChatRepository) {
    operator fun invoke() = repo.getMessages()
}
