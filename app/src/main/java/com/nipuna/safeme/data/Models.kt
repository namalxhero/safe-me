package com.nipuna.safeme.data

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val publicKey: String = ""
)

data class ChatSummary(
    val chatId: String = "",
    val title: String = "",
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)
