package everypin.app.network.model.post


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDetailDto(
    @Json(name = "createdDate")
    val createdDate: String?,
    @Json(name = "likeCount")
    val likeCount: Int?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "postContent")
    val postContent: String?,
    @Json(name = "postId")
    val postId: Int?,
    @Json(name = "postPhotos")
    val postPhotos: List<PostPhotoDto>?,
    @Json(name = "updateDate")
    val updateDate: Any?,
    @Json(name = "x")
    val x: Double?,
    @Json(name = "y")
    val y: Double?,
    @Json(name = "userId")
    val userId: String?
)