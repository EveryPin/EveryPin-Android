package everypin.app.network.model.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostWriteResponse(
    @SerialName(value = "createdDate")
    val createdDate: String?,
    @SerialName(value = "postContent")
    val postContent: String?,
    @SerialName(value = "postId")
    val postId: Int?,
    @SerialName(value = "updateDate")
    val updateDate: String?,
    @SerialName(value = "x")
    val x: Double?,
    @SerialName(value = "y")
    val y: Double?
)