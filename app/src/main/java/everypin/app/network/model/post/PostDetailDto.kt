package everypin.app.network.model.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailDto(
    @SerialName("createdDate")
    val createdDate: String,
    @SerialName("likeCount")
    val likeCount: Int,
    @SerialName("postContent")
    val postContent: String,
    @SerialName("postId")
    val postId: Int,
    @SerialName("postPhotos")
    val postPhotos: List<PostPhotoDto>,
    @SerialName("updateDate")
    val updateDate: String?,
    @SerialName("x")
    val x: Double,
    @SerialName("y")
    val y: Double,
    @SerialName("userId")
    val userId: String,
    @SerialName("writer")
    val writer: WriterDto
)