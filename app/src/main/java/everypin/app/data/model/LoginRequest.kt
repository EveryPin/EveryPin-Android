package everypin.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName(value = "platformCode")
    val platformCode: String,
    @SerialName(value = "accessToken")
    val accessToken: String,
    @SerialName(value = "fcmToken")
    val fcmToken: String
)
