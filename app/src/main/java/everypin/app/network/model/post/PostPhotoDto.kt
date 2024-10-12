package everypin.app.network.model.post

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostPhotoDto(
    @Json(name = "photoUrl")
    val photoUrl: String?,
    @Json(name = "postPhotoId")
    val postPhotoId: Int?
)