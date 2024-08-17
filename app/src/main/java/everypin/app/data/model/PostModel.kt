package everypin.app.data.model

import java.time.LocalDateTime

data class PostModel(
    val id: Int,
    val content: String,
    val createdDate: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
)
