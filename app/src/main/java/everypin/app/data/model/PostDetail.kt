package everypin.app.data.model

import java.time.LocalDateTime

data class PostDetail(
    val id: Int,
    val profileDisplayId: String,
    val content: String,
    val createdDate: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
    val photoUrls: List<String>,
    val likeCount: Int,
    val userId: String
)
