package everypin.app.network.model.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostPhotoDto(
    @SerialName(value = "photoUrl")
    val photoUrl: String?,
    @SerialName(value = "postPhotoId")
    val postPhotoId: Int?
)