package everypin.app.data.model

import java.time.LocalDateTime

data class PhotoPost(
    val id: Int,
    val content: String,
    val createdDate: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
    val photoUrls: List<String>
)
