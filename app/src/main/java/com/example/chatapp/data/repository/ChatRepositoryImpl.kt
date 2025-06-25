package com.example.chatapp.data.repository


import com.example.chatapp.data.model.MessageDto
import com.example.chatapp.data.remote.ChatRemoteDataSource
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val remote: ChatRemoteDataSource
) : ChatRepository {

    override suspend fun sendMessage(message: Message) {
        // map domain ➜ DTO
        remote.sendMessage(
            MessageDto(message.text, message.sender, message.timestamp)
        )
    }

    /** Firestore ➜ callbackFlow ➜ domain objects */
    override fun getMessages() = callbackFlow<List<Message>> {

        val registration = remote.messagesQuery()
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)           // propagate Firestore error
                    snapshot != null -> {
                        val list = snapshot.toObjects(MessageDto::class.java)
                            .map { dto ->
                                Message(dto.text, dto.sender, dto.timestamp)
                            }
                        trySend(list).isSuccess             // push to the Flow
                    }
                }
            }

        awaitClose { registration.remove() }                 // cancel listener when Flow closes
    }
}
