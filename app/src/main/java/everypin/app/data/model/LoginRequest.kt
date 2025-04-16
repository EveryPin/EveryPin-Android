package everypin.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("platformCode")
    val platformCode: String,
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("fcmToken")
    val fcmToken: String
)
