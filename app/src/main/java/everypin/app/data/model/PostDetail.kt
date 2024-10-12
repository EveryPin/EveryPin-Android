package everypin.app.data.model

import java.time.LocalDateTime

data class PostDetail(
    val id: Int,
    val name: String,
    val content: String,
    val createdDate: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
    val photoUrls: List<String>,
    val likeCount: Int
)
