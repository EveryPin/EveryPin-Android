package everypin.app.network.model.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostWriteResponse(
    @SerialName("createdDate")
    val createdDate: String?,
    @SerialName("postContent")
    val postContent: String?,
    @SerialName("postId")
    val postId: Int?,
    @SerialName("updateDate")
    val updateDate: String?,
    @SerialName("x")
    val x: Double?,
    @SerialName("y")
    val y: Double?
)