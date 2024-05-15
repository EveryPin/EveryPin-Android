package everypin.app.data.model

import java.time.LocalDateTime

data class ChatListModel(
    val id: Int,
    val name: String,
    val profileImgUrl: String?,
    val content: String,
    val lastChatDateTime: LocalDateTime
)