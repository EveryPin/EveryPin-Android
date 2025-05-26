package everypin.app.network.model.profile


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("createdDate")
    val createdDate: String?,
    @SerialName("photoUrl")
    val photoUrl: String?,
    @SerialName("profileDisplayId")
    val profileDisplayId: String,
    @SerialName("profileName")
    val profileName: String,
    @SerialName("selfIntroduction")
    val selfIntroduction: String?,
    @SerialName("updatedDate")
    val updatedDate: String?,
    @SerialName("email")
    val email: String
)