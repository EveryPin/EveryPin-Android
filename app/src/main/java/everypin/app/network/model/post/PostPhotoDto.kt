package everypin.app.network.model.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostPhotoDto(
    @SerialName("photoUrl")
    val photoUrl: String?,
    @SerialName("postPhotoId")
    val postPhotoId: Int?
)