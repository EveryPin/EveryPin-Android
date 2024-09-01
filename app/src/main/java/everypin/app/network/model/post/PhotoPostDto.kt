package everypin.app.network.model.post


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoPostDto(
    @Json(name = "createdDate")
    val createdDate: String?,
    @Json(name = "postContent")
    val postContent: String?,
    @Json(name = "postId")
    val postId: Int?,
    @Json(name = "postPhotos")
    val postPhotos: List<PostPhoto>?,
    @Json(name = "updateDate")
    val updateDate: Any?,
    @Json(name = "x")
    val x: Double?,
    @Json(name = "y")
    val y: Double?
) {
    @JsonClass(generateAdapter = true)
    data class PostPhoto(
        @Json(name = "photoUrl")
        val photoUrl: String?,
        @Json(name = "postPhotoId")
        val postPhotoId: Int?
    )
}